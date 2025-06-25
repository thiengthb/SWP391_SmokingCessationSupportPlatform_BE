package com.swpteam.smokingcessation.domain.dto.booking;

import com.swpteam.smokingcessation.validation.ValidDeclineReason;
import jakarta.validation.constraints.NotNull;

@ValidDeclineReason
public record BookingAnswerRequest(
        @NotNull(message = "BOOKING_DECISION_REQUIRED")
        Boolean accepted,
        String declineReason
) {
}
