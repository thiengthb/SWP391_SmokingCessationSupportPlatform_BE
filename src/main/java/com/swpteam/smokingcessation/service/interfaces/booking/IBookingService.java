package com.swpteam.smokingcessation.service.interfaces.booking;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
import com.swpteam.smokingcessation.domain.dto.booking.BookingResponse;
import com.swpteam.smokingcessation.domain.entity.Booking;
import com.swpteam.smokingcessation.domain.enums.BookingStatus;
import org.springframework.data.domain.Page;

public interface IBookingService {

    PageResponse<BookingResponse> getBookingPage(PageableRequest request);

    PageResponse<BookingResponse> getMyBookingPageAsMember(PageableRequest request);

    PageResponse<BookingResponse> getMyBookingPageAsCoach(PageableRequest request);

    BookingResponse getBookingById(String id);

    BookingResponse createBooking(BookingRequest request);

    BookingResponse updateBookingById(String id, BookingRequest request);

    BookingResponse updateMyBookingRequest(String id, BookingStatus status);

    void deleteBookingById(String id);

    BookingResponse createBookingWithMeet(BookingRequest request);

    Booking findBookingByIdOrThrowError(String id);
}