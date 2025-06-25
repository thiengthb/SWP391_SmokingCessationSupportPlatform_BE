package com.swpteam.smokingcessation.domain.dto.goal;

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
public class GoalResponse {

    String id;
    String name;
    String description;
    String iconUrl;
    String criteriaType;
    int criteriaValue;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    GoalProgressResponse goalProgress;
}
