package com.swpteam.smokingcessation.domain.dto.phase;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhaseTemplateResponse {
    int phase;
    String phaseName;
    int duration;
    int cigaretteBound;
    String description;
    List<String> tips;
}
