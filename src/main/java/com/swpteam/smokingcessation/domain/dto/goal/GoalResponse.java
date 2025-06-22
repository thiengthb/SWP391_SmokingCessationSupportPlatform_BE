package com.swpteam.smokingcessation.domain.dto.goal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalResponse {

    String id;
    String name;
    String description;
    String iconUrl;
    String criteriaType;
    int criteriaValue;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
