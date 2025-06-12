package com.swpteam.smokingcessation.apis.membership.dto;

import com.swpteam.smokingcessation.apis.currency.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MembershipCurrencyUpdateRequest {
    @NotNull(message = "CURRENCY_REQUIRED")
    Currency currency;
}
