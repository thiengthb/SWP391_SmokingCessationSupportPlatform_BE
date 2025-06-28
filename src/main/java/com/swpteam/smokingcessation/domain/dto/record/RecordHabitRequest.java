package com.swpteam.smokingcessation.domain.dto.record;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record RecordHabitRequest(

    @Min(value = 0, message = "CIGARETTES_SMOKED_INVALID")
    Integer cigarettesSmoked,

    String note,

    @NotNull(message = "RECORD_DATE_REQUIRED")
    @PastOrPresent(message = "RECORD_DATE_INVALID")
    LocalDate date
) {}