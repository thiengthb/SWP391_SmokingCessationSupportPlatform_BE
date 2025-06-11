package com.swpteam.smokingcessation.apis.membership.dto;

import com.swpteam.smokingcessation.apis.currency.Currency;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MembershipUpdateRequest {

    @Size(min = 1, message = "MEMBERSHIP_MIN_SIZE")
    String name;

    @Positive(message = "DURATION_NEGATIVE")
    Integer durationDays;

    @Positive(message = "PRICE_NEGATIVE")
    Double price;

    Currency currency;

    String description;
}
