package com.swpteam.smokingcessation.domain.dto.goal;

import jakarta.validation.constraints.*;

public record GoalCreateRequest(

    @NotBlank(message = "GOAL_NAME_REQUIRED")
    String name,

    @NotBlank(message = "GOAL_DESCRIPTION_REQUIRED")
    String description,

    @NotBlank(message = "GOAL_ICON_URL_REQUIRED")
    String iconUrl,

    @NotBlank(message = "GOAL_CRITERIA_TYPE_REQUIRED")
    String criteriaType,

    @NotNull(message = "GOAL_CRITERIA_VALUE_REQUIRED")
    @Min(value = 0, message = "GOAL_CRITERIA_VALUE_INVALID")
    Integer criteriaValue,

    String goalDifficulty
){}
