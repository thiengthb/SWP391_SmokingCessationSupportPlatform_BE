package com.swpteam.smokingcessation.domain.dto.plan;

import com.swpteam.smokingcessation.domain.dto.phase.PhaseRequest;
import jakarta.validation.constraints.*;

import java.util.List;

public record PlanRequest(

        @Size(max = 255, message = "PLAN_NAME_TOO_LONG")
        String planName,

        @Size(max = 255, message = "PLAN_DESCRIPTION_TOO_LONG")
        String description,

        @NotNull(message = "PHASE_REQUIRED")
        List<PhaseRequest> phases
) {}
