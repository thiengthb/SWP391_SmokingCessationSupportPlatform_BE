package com.swpteam.smokingcessation.domain.dto.phase;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhaseSummaryResponse {
    int phaseNo;
    String phaseName;
    LocalDate startDate;
    LocalDate endDate;
    long totalDaysReported;
    long totalDaysNotReported;
    int mostSmokeCig;
    int leastSmokeCig;
    Double successRate;
    PhaseStatus phaseStatus;
}
