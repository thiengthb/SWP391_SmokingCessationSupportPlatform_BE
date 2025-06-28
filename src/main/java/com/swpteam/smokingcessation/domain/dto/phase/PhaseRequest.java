package com.swpteam.smokingcessation.domain.dto.phase;

import com.swpteam.smokingcessation.domain.dto.tip.TipRequest;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record PhaseRequest(

        @Size(max = 50, message = "PHASE_NAME_TOO_LONG")
        String phaseName,

        @Size(max = 255, message = "PHASE_DESCRIPTION_TOO_LONG")
        String description,

        @Positive(message = "CIGARETTE_BOUND_INVALID")
        @NotNull(message = "PHASE_CIGARETTE_BOUND_REQUIRED")
        Integer cigaretteBound,

        @NotNull(message = "PHASE_START_DATE_REQUIRED")
        @FutureOrPresent(message = "PHASE_START_DATE_INVALID")
        LocalDate startDate,

        @NotNull(message = "PHASE_END_DATE_REQUIRED")
        @Future(message = "PHASE_END_DATE_INVALID")
        LocalDate endDate,

        List<TipRequest> tips

) {
}
