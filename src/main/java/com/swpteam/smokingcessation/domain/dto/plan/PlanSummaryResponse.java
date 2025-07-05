package com.swpteam.smokingcessation.domain.dto.plan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanSummaryResponse {
    String planName;
    LocalDate startDate;
    LocalDate endDate;
    long totalDaysReported;
    long totalDaysNotReported;
    int totalMostSmoked;
    int totalLeastSmoked;
    Double successRate;
    PlanStatus planStatus;
}
