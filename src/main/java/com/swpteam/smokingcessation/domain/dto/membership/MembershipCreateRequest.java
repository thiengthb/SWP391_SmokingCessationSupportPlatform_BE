package com.swpteam.smokingcessation.domain.dto.membership;

import com.swpteam.smokingcessation.domain.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MembershipCreateRequest (

    @NotBlank(message = "MEMBERSHIP_NAME_REQUIRED")
    @Size(min = 1, message = "MEMBERSHIP_NAME_TOO_SHORT")
    @Size(max = 30, message = "MEMBERSHIP_NAME_TOO_LONG")
    String name,

    @NotNull(message = "DURATION_REQUIRED")
    @Positive(message = "DURATION_INVALID")
    Integer durationDays,

    @NotNull(message = "PRICE_REQUIRED")
    @Positive(message = "PRICE_INVALID")
    Double price,

    @NotNull(message = "CURRENCY_REQUIRED")
    Currency currency,

    String description
) {}
