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
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanPageResponse {
    String id;
    String planName;
    LocalDate startDate;
    LocalDate endDate;
    PlanStatus planStatus;
}
