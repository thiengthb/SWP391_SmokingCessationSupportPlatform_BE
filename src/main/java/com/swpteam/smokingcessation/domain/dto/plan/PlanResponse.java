package com.swpteam.smokingcessation.domain.dto.plan;

import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanResponse {

    String id;
    String accountId;
    String planName;
    String description;
    double successRate;
    LocalDate startDate;
    LocalDate endDate;
    LocalDate createdAt;
    LocalDate updatedAt;
    PlanStatus planStatus;

}
