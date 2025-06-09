package com.swpteam.smokingcessation.apis.membership.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MembershipCreationRequest {
    @NotBlank(message = "MEMBERSHIP_NAME_NOT_EMPTY")
    @Size(min = 1, message = "MEMBERSHIP_MIN_SIZE")
    String name;

    @Positive(message = "DURATION_NEGATIVE")
    int duration;

    @Positive(message = "PRICE_NEGATIVE")
    double price;

    String description;
}
