package com.swpteam.smokingcessation.domain.dto.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanSummaryResponse {
    String accountId;
    String username;
    String email;

    String successRate;
    String leastSmokeDay;
    String mostSmokeDay;

    long reportedDays;
    long missedDays;

    String planStartDate;
    String planEndDate;
}
