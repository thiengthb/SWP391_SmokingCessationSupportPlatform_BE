package com.swpteam.smokingcessation.apis.membership.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MembershipUpdateRequest {
    @Positive(message = "DURATION_NEGATIVE")
    int duration;

    @Positive(message = "PRICE_NEGATIVE")
    double price;

    String description;
}
