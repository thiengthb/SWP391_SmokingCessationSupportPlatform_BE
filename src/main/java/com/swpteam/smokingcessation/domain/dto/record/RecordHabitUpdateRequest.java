package com.swpteam.smokingcessation.domain.dto.record;

import jakarta.validation.constraints.Min;

public record RecordHabitUpdateRequest (

    @Min(value = 0, message = "RECORD_CIGARETTES_SMOKED_INVALID")
    int cigarettesSmoked
) {}