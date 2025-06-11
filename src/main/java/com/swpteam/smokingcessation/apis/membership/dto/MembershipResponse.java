package com.swpteam.smokingcessation.apis.membership.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MembershipResponse {

    String id;
    String name;
    int durationDays;
    double price;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
