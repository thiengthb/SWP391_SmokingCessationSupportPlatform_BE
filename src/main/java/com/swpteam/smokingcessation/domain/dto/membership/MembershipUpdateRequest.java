package com.swpteam.smokingcessation.domain.dto.membership;

import com.swpteam.smokingcessation.domain.enums.Currency;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MembershipUpdateRequest (

    @Size(min = 1, message = "MEMBERSHIP_MIN_SIZE")
    String name,

    @Positive(message = "DURATION_NEGATIVE")
    Integer durationDays,

    @Positive(message = "PRICE_NEGATIVE")
    Double price,

    Currency currency,

    String description
) {}
