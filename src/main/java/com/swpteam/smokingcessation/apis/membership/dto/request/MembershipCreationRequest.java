package com.swpteam.smokingcessation.apis.membership.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MembershipCreationRequest {
    @NotBlank(message = "Name must not be empty")
    String name;

    @Positive(message = "Duration must be a positive number")
    int duration;

    @Positive(message = "Price must be a positive number")
    double price;

    String description;
}
