package com.swpteam.smokingcessation.controller.v1.booking;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
import com.swpteam.smokingcessation.domain.dto.booking.BookingResponse;
import com.swpteam.smokingcessation.service.interfaces.booking.IBookingService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Booking", description = "Manage booking-related operations")
public class BookingController {

    IBookingService bookingService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> getBookingPage(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.BOOKING_GET_ALL,
                bookingService.getBookingPage(request)
        );
    }

    @GetMapping("/coach-booking")
    ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> getBookingPageAsCoach(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.BOOKING_GET_ALL,
                bookingService.getMyBookingPageAsCoach(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getBookingById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.BOOKING_GET_BY_ID,
                bookingService.getBookingById(id)
        );
    }

    @PostMapping
    ResponseEntity<?> createBooking(
            @Valid @RequestBody BookingRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.BOOKING_CREATED,
                bookingService.createBooking(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateBookingById(
            @PathVariable String id,
            @Valid @RequestBody BookingRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.BOOKING_UPDATED,
                bookingService.updateBookingById(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> softDeleteBookingById(
            @PathVariable String id
    ) {
        bookingService.deleteBookingById(id);
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.BOOKING_DELETED,
                null
        );
    }

    @PostMapping("/with-meet")
    public ResponseEntity<ApiResponse<BookingResponse>> createBookingWithMeet(
            @Valid @RequestBody BookingRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.BOOKING_CREATED,
                bookingService.createBookingWithMeet(request)
        );
    }

}