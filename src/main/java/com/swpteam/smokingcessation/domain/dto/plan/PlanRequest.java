package com.swpteam.smokingcessation.domain.dto.plan;

import com.swpteam.smokingcessation.domain.dto.phase.PhaseRequest;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record PlanRequest(

        @Size(max = 255, message = "PLAN_NAME_TOO_LONG")
        String planName,

        @Size(max = 255, message = "PLAN_DESCRIPTION_TOO_LONG")
        String description,

        @NotNull(message = "SUCCESS_RATE_REQUIRED")
        @DecimalMin(value = "0.0", message = "SUCCESS_LEVEL_INVALID_MIN")
        @DecimalMax(value = "1.0", message = "SUCCESS_LEVEL_INVALID_MAX")
        double successRate,

        @NotNull(message = "PLAN_START_DATE_REQUIRED")
        @FutureOrPresent(message = "PLAN_START_DATE_MUST_BE_TODAY_OR_FUTURE")
        LocalDate startDate,

        @NotNull(message = "PLAN_END_DATE_REQUIRED")
        @Future(message = "PLAN_END_DATE_MUST_BE_IN_FUTURE")
        LocalDate endDate,

        @NotNull(message = "PLAN_STATUS_REQUIRED")
        PlanStatus planStatus,

        @NotNull(message = "PLAN_STATUS_REQUIRED")
        List<PhaseRequest> phases
) {}
