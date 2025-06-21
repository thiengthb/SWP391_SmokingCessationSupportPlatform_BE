package com.swpteam.smokingcessation.service.impl.booking;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
import com.swpteam.smokingcessation.domain.dto.booking.BookingResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Booking;
import com.swpteam.smokingcessation.domain.entity.Coach;
import com.swpteam.smokingcessation.domain.enums.BookingStatus;
import com.swpteam.smokingcessation.domain.mapper.BookingMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.repository.BookingRepository;
import com.swpteam.smokingcessation.repository.CoachRepository;
import com.swpteam.smokingcessation.repository.TimeTableRepository;
import com.swpteam.smokingcessation.service.interfaces.booking.IBookingService;
import com.swpteam.smokingcessation.service.interfaces.booking.ITimeTableService;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {

    BookingRepository bookingRepository;
    BookingMapper bookingMapper;
    GoogleCalendarService googleCalendarService;
    TimeTableRepository timeTableRepository;
    IAccountService accountService;
    AuthUtilService authUtilService;

    @Override
    public Page<BookingResponse> getBookingPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Booking.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Booking> bookings = bookingRepository.findAllByIsDeletedFalse(pageable);

        return bookings.map(bookingMapper::toResponse);
    }

    @Override
    public BookingResponse getBookingById(String id) {
        return bookingMapper.toResponse(findBookingByIdOrThrowError(id));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public BookingResponse createBooking(BookingRequest request) {
        // lấy account hiện tại (member)
        Account member = authUtilService.getCurrentAccountOrThrowError();

        // lấy account coach theo coachId từ request
        Account coach = accountService.findAccountByIdOrThrowError(request.coachId());

        // kiểm tra time hợp lệ (trong working time)
        boolean inWorkingTime = timeTableRepository
                .findByCoachIdAndStartedAtLessThanEqualAndEndedAtGreaterThanEqual(
                        request.coachId(), request.startedAt(), request.endedAt()
                ).isPresent();
        if (!inWorkingTime) {
            throw new AppException(ErrorCode.BOOKING_OUT_OF_WORKING_TIME);
        }

        // kiểm tra trùng lịch
        boolean isOverlapped = bookingRepository.existsByCoachIdAndIsDeletedFalseAndStartedAtLessThanAndEndedAtGreaterThan(
                request.coachId(),
                request.endedAt(),
                request.startedAt()
        );
        if (isOverlapped) {
            throw new AppException(ErrorCode.BOOKING_TIME_CONFLICT);
        }
        Booking booking = bookingMapper.toEntity(request);
        booking.setMember(member);
        booking.setCoach(coach);
        booking.setStatus(BookingStatus.PENDING);

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public BookingResponse updateBookingById(String id, BookingRequest request) {
        Booking booking = findBookingByIdOrThrowError(id);

        bookingMapper.update(booking, request);

        boolean haveAccess = authUtilService.isAdminOrOwner(booking.getMember().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        Account coach = accountService.findAccountByIdOrThrowError(request.coachId());
        booking.setCoach(coach);

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public void DeleteBookingById(String id) {
        Booking booking = findBookingByIdOrThrowError(id);

        bookingRepository.delete(booking);
    }

    @Override
    @Transactional
    public BookingResponse createBookingWithMeet(BookingRequest request) {
        Account member = authUtilService.getCurrentAccountOrThrowError();
        Account coach = accountService.findAccountByIdOrThrowError(request.coachId());

        String meetingUrl = null;
        try {
            meetingUrl = googleCalendarService.createGoogleMeetEvent(
                    request.accessToken(),
                    request.startedAt().toString(),
                    request.endedAt().toString()
            );
        } catch (Exception e) {
            throw new AppException(ErrorCode.GOOGLE_CALENDAR_ERROR);
        }

        Booking booking = bookingMapper.toEntity(request);
        booking.setMember(member);
        booking.setCoach(coach);
        booking.setMeetLink(meetingUrl);

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public Booking findBookingByIdOrThrowError(String id) {
        Booking booking = bookingRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getMember().isDeleted() || booking.getCoach().isDeleted()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return booking;
    }

}