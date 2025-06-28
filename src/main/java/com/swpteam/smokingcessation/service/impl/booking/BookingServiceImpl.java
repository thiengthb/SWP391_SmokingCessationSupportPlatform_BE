package com.swpteam.smokingcessation.service.impl.booking;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.booking.BookingAnswerRequest;
import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
import com.swpteam.smokingcessation.domain.dto.booking.BookingResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Booking;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.BookingStatus;
import com.swpteam.smokingcessation.domain.mapper.BookingMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.integration.google.GoogleCalendarService;
import com.swpteam.smokingcessation.integration.mail.IMailService;
import com.swpteam.smokingcessation.repository.BookingRepository;
import com.swpteam.smokingcessation.repository.TimeTableRepository;
import com.swpteam.smokingcessation.service.interfaces.booking.IBookingService;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.service.interfaces.notification.INotificationService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class BookingServiceImpl implements IBookingService {

    BookingRepository bookingRepository;
    TimeTableRepository timeTableRepository;
    BookingMapper bookingMapper;
    GoogleCalendarService googleCalendarService;
    IAccountService accountService;
    INotificationService notificationService;
    AuthUtilService authUtilService;
    IMailService mailService;

    @NonFinal
    @Value("${app.frontend-domain}")
    String FRONTEND_DOMAIN;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "BOOKING_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction")
    public PageResponse<BookingResponse> getBookingPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Booking.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Booking> bookings = bookingRepository.findAllByIsDeletedFalse(pageable);

        return new PageResponse<>(bookings.map(bookingMapper::toResponse));
    }

    @Override
    @PreAuthorize("hasRole('MEMBER')")
    @Cacheable(value = "BOOKING_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction + '-' + @authUtilService.getCurrentAccountOrThrowError().id")
    public PageResponse<BookingResponse> getMyBookingPageAsMember(PageableRequest request) {
        ValidationUtil.checkFieldExist(Booking.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Booking> bookings = bookingRepository.findAllByMemberIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(bookings.map(bookingMapper::toResponse));
    }

    @Override
    @PreAuthorize("hasRole('COACH')")
    @Cacheable(value = "BOOKING_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction + '-' + @authUtilService.getCurrentAccountOrThrowError().id")
    public PageResponse<BookingResponse> getMyBookingPageAsCoach(PageableRequest request) {
        ValidationUtil.checkFieldExist(Booking.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Booking> bookings = bookingRepository.findAllByCoachIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(bookings.map(bookingMapper::toResponse));
    }

    @Override
    @Cacheable(value = "BOOKING_CACHE", key = "#id")
    public BookingResponse getBookingById(String id) {
        return bookingMapper.toResponse(findBookingByIdOrThrowError(id));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @CachePut(value = "BOOKING_CACHE", key = "#result.getId()")
    @CacheEvict(value = "BOOKING_PAGE_CACHE", allEntries = true)
    public BookingResponse createBooking(BookingRequest request) {
        Account member = authUtilService.getCurrentAccountOrThrowError();
        Account coach = accountService.findAccountByIdOrThrowError(request.coachId());

        boolean inWorkingTime = timeTableRepository
                .findByCoach_IdAndStartedAtLessThanEqualAndEndedAtGreaterThanEqual(
                        request.coachId(), request.startedAt(), request.endedAt()
                ).isPresent();
        if (!inWorkingTime) {
            throw new AppException(ErrorCode.BOOKING_OUTSIDE_WORKING_HOURS);
        }

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

        BookingResponse response = bookingMapper.toResponse(bookingRepository.save(booking));

        if (coach.getStatus() == AccountStatus.ONLINE) {
            notificationService.sendBookingNotification(member.getUsername(), coach.getId());
        } else {
            String bookingLink = FRONTEND_DOMAIN + "/bookings?id=" + booking.getId();
            mailService.sendBookingRequestEmail(coach.getEmail(), request, member.getUsername(), coach.getUsername(), bookingLink);
        }
        return response;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('COACH')")
    @CachePut(value = "BOOKING_CACHE", key = "#result.getId()")
    @CacheEvict(value = "BOOKING_PAGE_CACHE", allEntries = true)
    public BookingResponse updateMyBookingRequestStatus(String id, BookingAnswerRequest request) {
        Booking booking = checkAndGetMyBooking(id);

        if (!booking.getStatus().equals(BookingStatus.PENDING)) {
            throw new AppException(ErrorCode.BOOKING_ALREADY_PROCESSED);
        }

        if (request.accepted()) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            booking.setDeclineReason(request.declineReason());
        }

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('MEMBER')")
    @CachePut(value = "BOOKING_CACHE", key = "#result.getId()")
    @CacheEvict(value = "BOOKING_PAGE_CACHE", allEntries = true)
    public BookingResponse updateBookingById(String id, BookingRequest request) {
        Booking booking = findBookingByIdOrThrowError(id);

        boolean haveAccess = authUtilService.isAdminOrOwner(booking.getMember().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        bookingMapper.update(booking, request);

        Account coach = accountService.findAccountByIdOrThrowError(request.coachId());
        booking.setCoach(coach);

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @CacheEvict(value = {"BOOKING_CACHE", "BOOKING_PAGE_CACHE"}, key = "#id", allEntries = true)
    public void deleteBookingById(String id) {
        Booking booking = findBookingByIdOrThrowError(id);

        booking.setDeleted(true);

        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    @CachePut(value = "BOOKING_CACHE", key = "#result.getId()")
    @CacheEvict(value = "BOOKING_PAGE_CACHE", allEntries = true)
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
    @Transactional
    public Booking findBookingByIdOrThrowError(String id) {
        Booking booking = bookingRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getMember().isDeleted() || booking.getCoach().isDeleted()) {
            booking.setDeleted(true);
            bookingRepository.save(booking);
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }

        return booking;
    }

    private Booking checkAndGetMyBooking(String id) {
        Booking booking = findBookingByIdOrThrowError(id);

        boolean haveAccess = authUtilService.isOwner(booking.getCoach().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return booking;
    }

}