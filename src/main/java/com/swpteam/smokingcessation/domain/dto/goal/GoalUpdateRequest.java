package com.swpteam.smokingcessation.domain.dto.goal;

import jakarta.validation.constraints.*;

public record GoalUpdateRequest(

    String name,

    String description,

    String iconUrl,

    String criteriaType,

    @Min(value = 0, message = "GOAL_CRITERIA_VALUE_INVALID")
    int criteriaValue
) {}
