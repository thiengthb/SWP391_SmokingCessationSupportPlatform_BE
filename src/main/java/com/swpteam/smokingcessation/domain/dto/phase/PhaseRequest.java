package com.swpteam.smokingcessation.domain.dto.phase;

import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PhaseRequest(

        @Size(max = 50, message = "PHASE_NAME_TOO_LONG")
        String phaseName,

        @Size(max = 255, message = "PHASE_DESCRIPTION_TOO_LONG")
        String description,

        @Positive(message = "CIGARETTE_NEGATIVE")
        @NotNull(message = "PHASE_CIGARETTE_BOUND_REQUIRED")
        Integer cigaretteBound,

        @NotNull(message = "PHASE_START_DATE_REQUIRED")
        //@FutureOrPresent(message = "PHASE_START_DATE_MUST_BE_TODAY_OR_FUTURE")
        //tắt để demo plan
        LocalDate startDate,

        @NotNull(message = "PHASE_END_DATE_REQUIRED")
        @Future(message = "PHASE_END_DATE_MUST_BE_IN_FUTURE")
        LocalDate endDate

) {
}
