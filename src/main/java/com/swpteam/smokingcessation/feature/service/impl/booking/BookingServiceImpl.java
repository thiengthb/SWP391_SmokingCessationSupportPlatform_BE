package com.swpteam.smokingcessation.feature.service.impl.booking;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
import com.swpteam.smokingcessation.domain.dto.booking.BookingResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Booking;
import com.swpteam.smokingcessation.domain.entity.Coach;
import com.swpteam.smokingcessation.domain.mapper.BookingMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.repository.AccountRepository;
import com.swpteam.smokingcessation.feature.repository.BookingRepository;
import com.swpteam.smokingcessation.feature.repository.CoachRepository;
import com.swpteam.smokingcessation.feature.service.interfaces.booking.BookingService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    BookingMapper bookingMapper;
    AccountRepository accountRepository;
    CoachRepository coachRepository;

    @Override
    public Page<BookingResponse> getBookingPage(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Booking.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Booking> bookings = bookingRepository.findAllByIsDeletedFalse(pageable);

        return bookings.map(bookingMapper::toResponse);
    }

    @Override
    public BookingResponse getBookingById(String id) {
        Booking booking = bookingRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        Booking booking = bookingMapper.toEntity(request);

        Account account = accountRepository.findByIdAndIsDeletedFalse(request.getAccountId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Coach coach = coachRepository.findByIdAndIsDeletedFalse(request.getCoachId())
                .orElseThrow(() -> new AppException(ErrorCode.COACH_NOT_FOUND));

        booking.setAccount(account);
        booking.setCoach(coach);

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse updateBookingById(String id, BookingRequest request) {
        Booking booking = bookingRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        bookingMapper.update(booking, request);

        Account account = accountRepository.findByIdAndIsDeletedFalse(request.getAccountId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Coach coach = coachRepository.findByIdAndIsDeletedFalse(request.getCoachId())
                .orElseThrow(() -> new AppException(ErrorCode.COACH_NOT_FOUND));

        booking.setAccount(account);
        booking.setCoach(coach);

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void softDeleteBookingById(String id) {
        Booking booking = bookingRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        booking.setDeleted(true);
        bookingRepository.save(booking);
    }
}