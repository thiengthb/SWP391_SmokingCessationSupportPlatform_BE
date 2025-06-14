package com.swpteam.smokingcessation.domain.dto.phase;

import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhaseResponse {

    String id;
    String planId;
    String phaseName;
    String description;
    Integer cigaretteBound;
    LocalDate startDate;
    LocalDate endDate;
    PhaseStatus phaseStatus;
    LocalDate createdAt;
    LocalDate updatedAt;
}