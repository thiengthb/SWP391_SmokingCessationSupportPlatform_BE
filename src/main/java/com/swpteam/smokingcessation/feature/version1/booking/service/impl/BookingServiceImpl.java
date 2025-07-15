    package com.swpteam.smokingcessation.feature.version1.booking.service.impl;

    import com.swpteam.smokingcessation.common.PageResponse;
    import com.swpteam.smokingcessation.common.PageableRequest;
    import com.swpteam.smokingcessation.constant.ErrorCode;
    import com.swpteam.smokingcessation.domain.dto.booking.BookingAnswerRequest;
    import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
    import com.swpteam.smokingcessation.domain.dto.booking.BookingResponse;
    import com.swpteam.smokingcessation.domain.entity.Account;
    import com.swpteam.smokingcessation.domain.entity.Booking;
    import com.swpteam.smokingcessation.domain.entity.TimeTable;
    import com.swpteam.smokingcessation.domain.enums.AccountStatus;
    import com.swpteam.smokingcessation.domain.enums.BookingStatus;
    import com.swpteam.smokingcessation.domain.mapper.BookingMapper;
    import com.swpteam.smokingcessation.exception.AppException;
    import com.swpteam.smokingcessation.feature.integration.google.GoogleCalendarService;
    import com.swpteam.smokingcessation.feature.integration.mail.IMailService;
    import com.swpteam.smokingcessation.feature.version1.booking.service.IBookingService;
    import com.swpteam.smokingcessation.feature.version1.booking.service.ITimeTableService;
    import com.swpteam.smokingcessation.feature.version1.identity.service.IAccountService;
    import com.swpteam.smokingcessation.feature.version1.notification.service.INotificationService;
    import com.swpteam.smokingcessation.repository.jpa.BookingRepository;
    import com.swpteam.smokingcessation.repository.jpa.TimeTableRepository;
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

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;

    @Slf4j
    @Service
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @RequiredArgsConstructor
    public class BookingServiceImpl implements IBookingService {

        BookingRepository bookingRepository;
        ITimeTableService timeTableService;
        BookingMapper bookingMapper;
        GoogleCalendarService googleCalendarService;
        IAccountService accountService;
        INotificationService notificationService;
        AuthUtilService authUtilService;
        IMailService mailService;
        TimeTableRepository timeTableRepository;

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

                        //check if this member already created booking that overlap?throw except
                        validateMemberBookingConflict(member.getId(), request.coachId(),
                                request.startedAt(), request.endedAt(), null);

                Booking booking = bookingMapper.toEntity(request);
                booking.setMember(member);
                booking.setCoach(coach);
                booking.setStatus(BookingStatus.PENDING);


                //check if booking coach_timetable
                if (timeTableService.isBookingTimeInAnyTimeTable(request.startedAt(), request.endedAt(), request.coachId())) {
                    booking.setStatus(BookingStatus.REJECTED);
                    booking.setDeclineReason(ErrorCode.COACH_IS_BUSY.toString());

                    Booking savedBooking = bookingRepository.save(booking);
                    sendRejectNotification(member, booking.getDeclineReason());

                return bookingMapper.toResponse(savedBooking);
            } else {
                Booking savedBooking = bookingRepository.save(booking);
                sendBookingRequestNotification(coach, member, savedBooking, request);
                return bookingMapper.toResponse(savedBooking);
            }
        }

        @Override
        @Transactional
        @PreAuthorize("hasRole('COACH')")
        @CachePut(value = "BOOKING_CACHE", key = "#result.getId()")
        @CacheEvict(value = "BOOKING_PAGE_CACHE", allEntries = true)
        public BookingResponse updateMyBookingRequestStatus(String id, BookingAnswerRequest request) {
            Booking booking = findBookingByIdOrThrowError(id);

            if (booking.getStatus() != BookingStatus.PENDING) {
                throw new AppException(ErrorCode.BOOKING_ALREADY_IN_PROCESS);
            }

            if (!authUtilService.isOwner(booking.getCoach().getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            if (Boolean.TRUE.equals(request.accepted())) {
                booking.setStatus(BookingStatus.APPROVED);

                TimeTable timeTable = timeTableService.createTimeTableAuto(booking.getStartedAt(), booking.getEndedAt(), booking.getCoach(),booking);
                booking.setTimeTable(timeTable);
                List<Booking> pendingConflicts = bookingRepository.findAllByCoachIdAndStatusAndIsDeletedFalse(
                        booking.getCoach().getId(),
                        BookingStatus.PENDING
                );
                //reject other booking in the accepted booking period
                List<Booking> toReject = new ArrayList<>();
                for (Booking other : pendingConflicts) {

                    if (other.getId().equals(id)) continue;

                    boolean overlaps = !(booking.getEndedAt().isBefore(other.getStartedAt()) ||
                            booking.getStartedAt().isAfter(other.getEndedAt()));

                    if (overlaps) {
                        other.setStatus(BookingStatus.REJECTED);
                        other.setDeclineReason(ErrorCode.COACH_IS_BUSY.toString());
                        toReject.add(other);
                        sendRejectNotification(other.getMember(), other.getDeclineReason());
                    }
                }
                log.info("Before sending email notification - time={}", LocalDateTime.now());
                SendApprovedNotification(booking.getMember(), booking.getCoach());
                log.info("After sending email notification - time={}", LocalDateTime.now());
                bookingRepository.saveAll(toReject);

            } else {
                booking.setStatus(BookingStatus.REJECTED);
                booking.setDeclineReason(request.declineReason());
                sendRejectNotification(booking.getMember(), booking.getDeclineReason());

            }
            log.info("response at:{}",LocalDateTime.now());
            return bookingMapper.toResponse(bookingRepository.save(booking));

        }

        @Override
        @Transactional
        @PreAuthorize("hasRole('MEMBER')")
        @CachePut(value = "BOOKING_CACHE", key = "#result.getId()")
        @CacheEvict(value = "BOOKING_PAGE_CACHE", allEntries = true)
        public BookingResponse updateBookingById(String id, BookingRequest request) {
            Booking booking = findBookingByIdOrThrowError(id);

            if (booking.getStatus() != BookingStatus.PENDING) {
                throw new AppException(ErrorCode.BOOKING_ALREADY_IN_PROCESS);
            }

            boolean haveAccess = authUtilService.isAdminOrOwner(booking.getMember().getId());
            if (!haveAccess) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            validateMemberBookingConflict(
                    booking.getMember().getId(),
                    request.coachId(),
                    request.startedAt(),
                    request.endedAt(),
                    id
            );
            if (timeTableService.isBookingTimeInAnyTimeTable(request.startedAt(), request.endedAt(), request.coachId())) {
                throw new AppException(ErrorCode.COACH_IS_BUSY);
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

        //if booking == approved , delete -> notify -> delete timetable
            //if not => delete
            if (booking.getStatus() == BookingStatus.APPROVED) {
                notificationService.sendBookingCancelledNotification(
                        booking.getCoach().getId(),
                        booking.getMember().getUsername()
                );

                mailService.sendBookingCancelledEmail(
                        booking.getCoach().getEmail(),
                        booking.getMember().getUsername(),
                        booking.getStartedAt(),
                        booking.getEndedAt()
                );

                Optional<TimeTable> optionalTimeTable = timeTableRepository
                        .findByCoach_IdAndStartedAtAndEndedAtAndIsDeletedFalse(
                                booking.getCoach().getId(),
                                booking.getStartedAt(),
                                booking.getEndedAt()
                        );

                optionalTimeTable.ifPresent(timeTable -> {
                    timeTable.setDeleted(true);
                    timeTableRepository.save(timeTable);
                });
            }

            booking.setDeleted(true);
            bookingRepository.save(booking);
        }


        @Override
        public Booking findBookingByIdOrThrowError(String id) {

            return bookingRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        }

        private Booking checkAndGetMyBooking(String id) {
            Booking booking = findBookingByIdOrThrowError(id);

            boolean haveAccess = authUtilService.isOwner(booking.getCoach().getId());
            if (!haveAccess) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            return booking;
        }

        /*
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


         */
        private void validateMemberBookingConflict(String memberId, String coachId,
                                                   LocalDateTime startedAt, LocalDateTime endedAt,
                                                   String excludeBookingId) {
            //take booking list of this account to this coachId
            List<Booking> memberPendingBookings = bookingRepository.findAllByMemberIdAndCoachIdAndIsDeletedFalse(
                    memberId, coachId);

            for (Booking existingBooking : memberPendingBookings) {
                    //if id=updateBookingId => skip
                if (existingBooking.getId().equals(excludeBookingId)) {
                    continue;
                }

                //check overlap
                boolean overlaps = !(endedAt.isBefore(existingBooking.getStartedAt()) ||
                        startedAt.isAfter(existingBooking.getEndedAt()));

                if (overlaps) {
                    throw new AppException(ErrorCode.BOOKING_TIME_CONFLICT);
                }
            }
        }

        private void sendRejectNotification(Account member, String reason) {
            if (member.getStatus() == AccountStatus.ONLINE) {
                notificationService.sendBookingRejectNotification(reason, member.getId());
            } else {
                mailService.sendRejectNotificationMail(member.getEmail(), reason);
            }
        }

        private void SendApprovedNotification(Account member, Account coach) {
                notificationService.sendApprovedNotification(member.getId(), coach.getUsername());
                mailService.sendApprovedNotificationMail(member.getEmail(), coach.getUsername());
            }


        private void sendBookingRequestNotification(Account coach, Account member,
                                                    Booking savedBooking, BookingRequest request) {
            if (coach.getStatus() == AccountStatus.ONLINE) {
                notificationService.sendBookingNotification(member.getUsername(), coach.getId());
            } else {
                String bookingLink = FRONTEND_DOMAIN + "/bookings?id=" + savedBooking.getId();
                mailService.sendBookingRequestEmail(
                        coach.getEmail(),
                        request,
                        member.getUsername(),
                        coach.getUsername(),
                        bookingLink
                );
            }
        }
    }