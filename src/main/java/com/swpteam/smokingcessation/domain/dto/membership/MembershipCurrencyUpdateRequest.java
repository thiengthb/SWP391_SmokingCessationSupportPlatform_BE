package com.swpteam.smokingcessation.domain.dto.membership;

import com.swpteam.smokingcessation.domain.enums.Currency;
import jakarta.validation.constraints.NotNull;

public record MembershipCurrencyUpdateRequest (

    @NotNull(message = "CURRENCY_REQUIRED")
    Currency currency
) {}
