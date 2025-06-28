package com.swpteam.smokingcessation.domain.dto.membership;

import com.swpteam.smokingcessation.domain.enums.Currency;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MembershipUpdateRequest (

    @Size(min = 1, message = "MEMBERSHIP_NAME_TOO_SHORT")
    @Size(max = 30, message = "MEMBERSHIP_NAME_TOO_LONG")
    String name,

    @Positive(message = "DURATION_INVALID")
    Integer durationDays,

    @Positive(message = "PRICE_INVALID")
    Double price,

    Currency currency,

    String description
) {}
