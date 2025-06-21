package com.swpteam.smokingcessation.service.impl.booking;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;

import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableRequest;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Booking;
import com.swpteam.smokingcessation.domain.entity.TimeTable;
import com.swpteam.smokingcessation.domain.mapper.TimeTableMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.TimeTableRepository;
import com.swpteam.smokingcessation.service.interfaces.booking.ITimeTableService;

import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TimeTableServiceImpl implements ITimeTableService {

    TimeTableRepository timeTableRepository;
    TimeTableMapper timeTableMapper;
    AuthUtilService authUtilService;


    @Override
    public Page<TimeTableResponse> getTimeTablePage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Booking.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<TimeTable> timeTables = timeTableRepository.findAllByIsDeletedFalse(pageable);

        return timeTables.map(timeTableMapper::toResponse);
    }

    @Override
    public TimeTableResponse getTimeTableById(String id) {
        return timeTableMapper.toResponse(findTimeTableByIdOrThrowError(id));
    }

    @Override
    public Page<TimeTableResponse> getTimeTablesByCoachId(String coachId, PageableRequest request) {
        ValidationUtil.checkFieldExist(Booking.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<TimeTable> timeTables = timeTableRepository.findByCoachIdAndIsDeletedFalse(coachId, pageable);

        return timeTables.map(timeTableMapper::toResponse);
    }


    @Override
    @Transactional
    @PreAuthorize("hasRole('COACH')")
    public TimeTableResponse createTimeTable(TimeTableRequest request) {
        TimeTable timeTable = timeTableMapper.toEntity(request);
        Account coach=authUtilService.getCurrentAccountOrThrowError();
        timeTable.setCoach(coach);

        return timeTableMapper.toResponse(timeTableRepository.save(timeTable));
    }


    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    public TimeTableResponse updateTimeTableById(String id, TimeTableRequest request) {
        TimeTable timeTable = findTimeTableByIdOrThrowError(id);
        boolean haveAccess = authUtilService.isAdminOrOwner(timeTable.getCoach().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        Account coach=authUtilService.getCurrentAccountOrThrowError();
        timeTableMapper.update(timeTable, request);
        timeTable.setCoach(coach);

        return timeTableMapper.toResponse(timeTableRepository.save(timeTable));
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    public void softDeleteTimeTableById(String id) {
        TimeTable timeTable = findTimeTableByIdOrThrowError(id);

        timeTable.setDeleted(true);

        timeTableRepository.save(timeTable);
    }

    public TimeTable findTimeTableByIdOrThrowError(String id) {
        TimeTable timeTable = timeTableRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()->new AppException(ErrorCode.TIMETABLE_NOT_FOUND));
        if ( timeTable.getCoach().isDeleted()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return timeTable;
    }
}
