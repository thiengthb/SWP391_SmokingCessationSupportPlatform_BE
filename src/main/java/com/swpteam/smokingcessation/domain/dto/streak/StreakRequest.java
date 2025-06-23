package com.swpteam.smokingcessation.domain.dto.streak;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StreakRequest (

    @Min(value = 0, message = "STREAK_INVALID")
    @NotNull(message = "STREAK_REQUIRED")
    Integer streak
) {}
