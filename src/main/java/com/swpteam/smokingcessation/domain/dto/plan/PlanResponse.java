package com.swpteam.smokingcessation.domain.dto.plan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthResponse;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.domain.entity.Health;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanResponse {

    String id;
    String accountId;
    String planName;
    String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Double successRate;

    LocalDate startDate;
    LocalDate endDate;
    LocalDate createdAt;
    LocalDate updatedAt;
    PlanStatus planStatus;
    Double progress;

    List<PhaseResponse> phases;
}