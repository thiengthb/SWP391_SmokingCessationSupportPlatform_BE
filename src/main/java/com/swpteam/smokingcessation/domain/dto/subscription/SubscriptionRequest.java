package com.swpteam.smokingcessation.domain.dto.subscription;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record SubscriptionRequest (

    @Email(message = "INVALID_EMAIL_FORMAT")
    @NotBlank(message = "EMAIL_REQUIRED")
    String email,

    @NotBlank(message = "MEMBERSHIP_REQUIRED")
    @Size(min = 1, message = "MEMBERSHIP_MIN_SIZE")
    String membershipName,

    @NotNull(message = "SUBSCRIPTION_START_DATE_REQUIRED")
    @FutureOrPresent(message = "SUBSCRIPTION_START_DATE_INVALID")
    LocalDate startDate,

    @NotNull(message = "SUBSCRIPTION_END_DATE_REQUIRED")
    @Future(message = "SUBSCRIPTION_END_DATE_INVALID")
    LocalDate endDate
) {}
