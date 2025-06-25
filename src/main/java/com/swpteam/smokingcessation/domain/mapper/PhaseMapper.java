package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.phase.PhaseRequest;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.domain.entity.Phase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PhaseMapper {
    @Mapping(target = "successRate", expression = "java(roundToTwoDecimal(phase.getSuccessRate()))")
    @Mapping(source = "phase", target = "phase")
    @Mapping(source = "plan.id",target = "planId")
    PhaseResponse toResponse(Phase phase);

    Phase toEntity(PhaseRequest phaseRequest);

    void update(@MappingTarget Phase phase, PhaseRequest request);

    default double roundToTwoDecimal(Double value) {
        return value == null ? 0.0 : Math.round(value * 100.0) / 100.0;
    }
}