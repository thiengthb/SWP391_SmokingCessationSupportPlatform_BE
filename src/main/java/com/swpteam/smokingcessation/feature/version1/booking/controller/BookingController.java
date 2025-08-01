package com.swpteam.smokingcessation.feature.version1.booking.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.booking.BookingAnswerRequest;
import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
import com.swpteam.smokingcessation.domain.dto.booking.BookingResponse;
import com.swpteam.smokingcessation.feature.version1.booking.service.IBookingService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
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
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> getBookingPage(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BOOKING_PAGE_FETCHED,
                bookingService.getBookingPage(request)
        );
    }

    @GetMapping("/member-booking")
    ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> getBookingPageAsMember(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BOOKING_PAGE_FETCHED,
                bookingService.getMyBookingPageAsMember(request)
        );
    }

    @GetMapping("/coach-booking")
    ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> getBookingPageAsCoach(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BOOKING_PAGE_FETCHED,
                bookingService.getMyBookingPageAsCoach(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getBookingById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BOOKING_FETCHED_BY_ID,
                bookingService.getBookingById(id)
        );
    }

    @PostMapping
    ResponseEntity<?> createBooking(
            @Valid @RequestBody BookingRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BOOKING_CREATED,
                bookingService.createBooking(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateBookingById(
            @PathVariable String id,
            @Valid @RequestBody BookingRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BOOKING_UPDATED,
                bookingService.updateBookingById(id, request)
        );
    }

    @PutMapping("/answer/{id}")
    ResponseEntity<?> answerBookingRequest(
            @PathVariable String id,
            @Valid @RequestBody BookingAnswerRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BOOKING_ANSWERED,
                bookingService.updateMyBookingRequestStatus(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> softDeleteBookingById(
            @PathVariable String id
    ) {
        bookingService.deleteBookingById(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BOOKING_DELETED
        );
    }

  /*  @PostMapping("/with-meet")
    public ResponseEntity<ApiResponse<BookingResponse>> createBookingWithMeet(
            @Valid @RequestBody BookingRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BOOKING_CREATED,
                bookingService.createBookingWithMeet(request)
        );
    }

   */

}