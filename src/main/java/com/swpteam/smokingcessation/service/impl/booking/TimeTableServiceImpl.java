package com.swpteam.smokingcessation.service.impl.booking;

import com.swpteam.smokingcessation.common.PageResponse;
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

import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TimeTableServiceImpl implements ITimeTableService {

    TimeTableRepository timeTableRepository;
    TimeTableMapper timeTableMapper;
    IAccountService accountService;
    AuthUtilService authUtilService;

    @Override
    @Cacheable(value = "TIMETABLE_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction")
    public PageResponse<TimeTableResponse> getTimeTablePage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Booking.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<TimeTable> timeTables = timeTableRepository.findAllByIsDeletedFalse(pageable);

        return new PageResponse<>(timeTables.map(timeTableMapper::toResponse));
    }

    @Override
    @PreAuthorize("hasRole('COACH')")
    @Cacheable(value = "TIMETABLE_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction + '-' + @authUtilService.getCurrentAccountOrThrowError().id")
    public PageResponse<TimeTableResponse> getMyTimeTablePage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Booking.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<TimeTable> timeTables = timeTableRepository.findAllByCoach_IdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(timeTables.map(timeTableMapper::toResponse));
    }

    @Override
    @Cacheable(value = "TIMETABLE_PAGE_CACHE",
            key = "#coachId + '-' + #request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction")
    public PageResponse<TimeTableResponse> getTimeTablesByCoachId(String coachId, PageableRequest request) {
        ValidationUtil.checkFieldExist(Booking.class, request.sortBy());

        accountService.findAccountByIdOrThrowError(coachId);

        Pageable pageable = PageableRequest.getPageable(request);
        Page<TimeTable> timeTables = timeTableRepository.findByCoach_IdAndIsDeletedFalse(coachId, pageable);

        return new PageResponse<>(timeTables.map(timeTableMapper::toResponse));
    }

    @Override
    @Cacheable(value = "TIMETABLE_CACHE", key = "#id")
    public TimeTableResponse getTimeTableById(String id) {
        return timeTableMapper.toResponse(findTimeTableByIdOrThrowError(id));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('COACH')")
    @CachePut(value = "TIMETABLE_CACHE", key = "#result.getId()")
    @CacheEvict(value = "TIMETABLE_PAGE_CACHE", allEntries = true)
    public TimeTableResponse createTimeTable(TimeTableRequest request) {
        Account coach = authUtilService.getCurrentAccountOrThrowError();

        TimeTable timeTable = timeTableMapper.toEntity(request);
        timeTable.setCoach(coach);

        return timeTableMapper.toResponse(timeTableRepository.save(timeTable));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    @CachePut(value = "TIMETABLE_CACHE", key = "#result.getId()")
    @CacheEvict(value = "TIMETABLE_PAGE_CACHE", allEntries = true)
    public TimeTableResponse updateTimeTableById(String id, TimeTableRequest request) {
        TimeTable timeTable = findTimeTableByIdOrThrowError(id);

        boolean haveAccess = authUtilService.isAdminOrOwner(timeTable.getCoach().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        timeTableMapper.update(timeTable, request);

        Account coach = authUtilService.getCurrentAccountOrThrowError();
        timeTable.setCoach(coach);
        return timeTableMapper.toResponse(timeTableRepository.save(timeTable));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    @CacheEvict(value = {"TIMETABLE_CACHE", "TIMETABLE_PAGE_CACHE"}, key = "#id", allEntries = true)
    public void softDeleteTimeTableById(String id) {
        TimeTable timeTable = findTimeTableByIdOrThrowError(id);

        boolean haveAccess = authUtilService.isAdminOrOwner(timeTable.getCoach().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        timeTable.setDeleted(true);
        timeTableRepository.save(timeTable);
    }

    @Override
    @Transactional
    public TimeTable findTimeTableByIdOrThrowError(String id) {
        TimeTable timeTable = timeTableRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()->new AppException(ErrorCode.TIMETABLE_NOT_FOUND));

        if (timeTable.getCoach().isDeleted()) {
            timeTable.setDeleted(true);
            timeTableRepository.save(timeTable);
            throw new AppException(ErrorCode.TIMETABLE_NOT_FOUND);
        }

        return timeTable;
    }

}
