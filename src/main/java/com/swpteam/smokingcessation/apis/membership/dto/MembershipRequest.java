package com.swpteam.smokingcessation.apis.membership.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MembershipRequest {

    @NotBlank(message = "MEMBERSHIP_NAME_NOT_EMPTY")
    @Size(min = 1, message = "MEMBERSHIP_MIN_SIZE")
    String name;

    @NotNull(message = "DURATION_REQUIRED")
    @Positive(message = "DURATION_NEGATIVE")
    Integer durationDays;

    @NotNull(message = "PRICE_REQUIRED")
    @Positive(message = "PRICE_NEGATIVE")
    Double price;

    String description;
}
