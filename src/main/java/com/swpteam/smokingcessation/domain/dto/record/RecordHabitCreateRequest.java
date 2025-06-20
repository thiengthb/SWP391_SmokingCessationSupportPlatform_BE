package com.swpteam.smokingcessation.domain.dto.record;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RecordHabitCreateRequest (

    @NotBlank(message = "ACCOUNT_REQUIRED")
    String accountId,

    @NotNull(message = "CIGARETTES_SMOKED_REQUIRED")
    @Min(value = 0, message = "CIGARETTES_SMOKED_INVALID")
    Integer cigarettesSmoked,

    @NotNull(message = "RECORD_DATE_REQUIRED")
    @FutureOrPresent(message = "RECORD_DATE_INVALID")
    LocalDate date
) {}