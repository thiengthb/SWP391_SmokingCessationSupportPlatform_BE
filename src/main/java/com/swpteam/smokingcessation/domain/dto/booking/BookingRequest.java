package com.swpteam.smokingcessation.domain.dto.booking;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record BookingRequest (

    @NotBlank(message = "COACH_ACCOUNT_ID_REQUIRED")
    String coachId,

   // String meetLink,

    @NotNull(message = "BOOKING_START_DATE_REQUIRED")
    @FutureOrPresent(message = "BOOKING_START_DATE_INVALID")
    LocalDateTime startedAt,

    @NotNull(message = "BOOKING_END_DATE_REQUIRED")
    @Future(message = "BOOKING_END_DATE_INVALID")
    LocalDateTime endedAt,

    String bookingReason

    //String accessToken // access token Google để tạo Google Meet

) {}