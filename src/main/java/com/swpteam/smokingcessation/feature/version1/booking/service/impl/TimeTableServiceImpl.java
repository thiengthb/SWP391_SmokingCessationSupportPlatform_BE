package com.swpteam.smokingcessation.feature.version1.booking.service.impl;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;

import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableRequest;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Booking;
import com.swpteam.smokingcessation.domain.entity.Coach;
import com.swpteam.smokingcessation.domain.entity.TimeTable;
import com.swpteam.smokingcessation.domain.mapper.TimeTableMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.integration.mail.IMailService;
import com.swpteam.smokingcessation.feature.integration.mail.MailServiceImpl;
import com.swpteam.smokingcessation.feature.version1.booking.service.ITimeTableService;
import com.swpteam.smokingcessation.feature.version1.notification.service.INotificationService;
import com.swpteam.smokingcessation.repository.jpa.BookingRepository;
import com.swpteam.smokingcessation.repository.jpa.TimeTableRepository;

import com.swpteam.smokingcessation.feature.version1.identity.service.IAccountService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TimeTableServiceImpl implements ITimeTableService {

    TimeTableRepository timeTableRepository;
    TimeTableMapper timeTableMapper;
    IAccountService accountService;
    AuthUtilService authUtilService;
    BookingRepository bookingRepository;
    INotificationService notificationService;
    IMailService mailService;


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

        List<TimeTable> timeTables = timeTableRepository.getAllByCoachIdAndIsDeletedFalse(coach.getId());
        for (TimeTable tt : timeTables) {
            boolean overlaps = !(request.endedAt().isBefore(tt.getStartedAt()) ||
                    request.startedAt().isAfter(tt.getEndedAt()));
            if (overlaps) {
                throw new AppException(ErrorCode.TIMETABLE_TIME_CONFLICT);
            }
        }

        TimeTable timeTable = timeTableMapper.toEntity(request);
        timeTable.setCoach(coach);

        return timeTableMapper.toResponse(timeTableRepository.save(timeTable));
    }

    @Override
    @PreAuthorize("hasRole('COACH')")
    //@CachePut(value = "TIMETABLE_CACHE", key = "#result.getId()")
    @CacheEvict(value = "TIMETABLE_PAGE_CACHE", allEntries = true)
    public TimeTable createTimeTableAuto(LocalDateTime start, LocalDateTime end, Account coach,Booking booking) {
        TimeTable timeTable = TimeTable.builder()
                .name("booking with member")
                .description("You have a booking schedule with member during this time")
                .startedAt(start)
                .endedAt(end)
                .coach(coach)
                .booking(booking)
                .build();
        return timeTableRepository.save(timeTable);


    }

    public boolean isBookingTimeInAnyTimeTable(LocalDateTime bookingStart, LocalDateTime bookingEnd, String coachId) {
        List<TimeTable> timeTables = timeTableRepository.getAllByCoachIdAndIsDeletedFalse(coachId);
        for (TimeTable tt : timeTables) {
            boolean overlaps = !(bookingEnd.isBefore(tt.getStartedAt()) || bookingStart.isAfter(tt.getEndedAt()));
            if (overlaps) {
                return true;
            }
        }
        return false;
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
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<TimeTable> existingTimeTables = timeTableRepository.getAllByCoachIdAndIsDeletedFalse(timeTable.getCoach().getId());
        for (TimeTable existing : existingTimeTables) {
            if (existing.getId().equals(id)) continue;

            boolean overlaps = !(request.endedAt().isBefore(existing.getStartedAt()) ||
                    request.startedAt().isAfter(existing.getEndedAt()));
            if (overlaps) {
                throw new AppException(ErrorCode.TIMETABLE_TIME_CONFLICT);
            }
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
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Booking linkedBooking = timeTable.getBooking();
        if (linkedBooking != null && !linkedBooking.isDeleted()) {
            linkedBooking.setDeleted(true);
            bookingRepository.save(linkedBooking);
            notificationService.sendBookingRejectNotification("Your booking has been cancelled",linkedBooking.getMember().getId());
            mailService.sendRejectNotificationMail(linkedBooking.getMember().getEmail(),"Coach mắc việc đột xuất");
        }

        timeTable.setDeleted(true);
        timeTableRepository.save(timeTable);
    }

    @Override
    @Transactional
    public TimeTable findTimeTableByIdOrThrowError(String id) {
        TimeTable timeTable = timeTableRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.TIMETABLE_NOT_FOUND));

        if (timeTable.getCoach().isDeleted()) {
            timeTable.setDeleted(true);
            timeTableRepository.save(timeTable);
            throw new AppException(ErrorCode.TIMETABLE_NOT_FOUND);
        }

        return timeTable;
    }


}
