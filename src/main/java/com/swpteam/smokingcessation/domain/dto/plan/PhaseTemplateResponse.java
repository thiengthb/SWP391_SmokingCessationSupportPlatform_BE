package com.swpteam.smokingcessation.domain.dto.plan;

import lombok.Data;

@Data
public class PhaseTemplateResponse {
    int phase;
    String cigaretteBound;
    String duration;
}
