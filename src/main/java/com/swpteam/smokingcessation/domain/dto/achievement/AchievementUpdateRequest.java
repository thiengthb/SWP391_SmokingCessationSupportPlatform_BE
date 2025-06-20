package com.swpteam.smokingcessation.domain.dto.achievement;

import jakarta.validation.constraints.*;

public record AchievementUpdateRequest (

    String name,

    String description,

    String iconUrl,

    String criteriaType,

    @Min(value = 0, message = "CRITERIA_VALUE_INVALID")
    int criteriaValue
) {}
