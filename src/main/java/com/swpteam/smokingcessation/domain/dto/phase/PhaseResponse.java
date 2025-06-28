package com.swpteam.smokingcessation.domain.dto.phase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.domain.dto.tip.TipResponse;
import com.swpteam.smokingcessation.domain.entity.Tip;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhaseResponse {
    String id;
    String planId;
    int phase;
    LocalDate startDate;
    LocalDate endDate;
    String phaseName;
    String description;
    Integer cigaretteBound;
    PhaseStatus phaseStatus;
    @JsonIgnore
    double successRate;
    List<TipResponse> tips;
}