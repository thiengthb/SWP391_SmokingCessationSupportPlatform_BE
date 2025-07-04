package com.swpteam.smokingcessation.domain.dto.plan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseTemplateResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanTemplateResponse {
    String planName;
    String description;
    int totalDuration;
    List<PhaseTemplateResponse> phases;
}
