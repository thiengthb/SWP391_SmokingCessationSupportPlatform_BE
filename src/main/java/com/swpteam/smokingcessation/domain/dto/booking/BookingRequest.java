package com.swpteam.smokingcessation.domain.dto.booking;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record BookingRequest (

    @NotBlank(message = "COACH_ID_REQUIRED")
    String coachId,

    String meetLink,

    @NotNull(message = "STARTED_AT_REQUIRED")
    @FutureOrPresent(message = "STARTED_AT_MUST_BE_TODAY_OR_FUTURE")
    LocalDateTime startedAt,

    @NotNull(message = "ENDED_AT_REQUIRED")
    @Future(message = "ENDED_AT_MUST_BE_IN_FUTURE")
    LocalDateTime endedAt,

    @NotNull(message = "IS_APPROVED_REQUIRED")
    boolean isApproved,

    String accessToken // access token Google để tạo Google Meet

) {}