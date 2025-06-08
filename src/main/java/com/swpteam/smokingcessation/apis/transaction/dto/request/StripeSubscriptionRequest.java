package com.swpteam.smokingcessation.apis.transaction.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeSubscriptionRequest {

    @NotNull(message = "AMOUNT_REQUIRED")
    @Positive(message = "AMOUNT_NEGATIVE")
    Long amount;

    @NotNull(message = "NAME_REQUIRED")
    String name;

    @NotNull(message = "CURRENCY_REQUIRED")
    String currency;
}
