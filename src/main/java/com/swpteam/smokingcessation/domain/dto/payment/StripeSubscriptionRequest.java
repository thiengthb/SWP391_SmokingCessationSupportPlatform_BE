package com.swpteam.smokingcessation.domain.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StripeSubscriptionRequest (

    @NotBlank(message = "ACCOUNT_REQUIRED")
    String accountId,

    @NotBlank(message = "MEMBERSHIP_NAME_REQUIRED")
    @Size(min = 1, message = "MEMBERSHIP_NAME_TOO_SHORT")
    String membershipName
) {}
