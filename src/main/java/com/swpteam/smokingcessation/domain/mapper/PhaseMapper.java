package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.phase.PhaseRequest;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.Phase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",uses = TipMapper.class)
public interface PhaseMapper {

    @Mapping(target = "successRate", expression = "java(roundToTwoDecimal(phase.getSuccessRate()))")
    @Mapping(source = "plan.id",target = "planId")
    PhaseResponse toResponse(Phase phase);

    @Mapping(target = "tips", source = "tips")
    Phase toEntity(PhaseRequest phaseRequest);

    void update(@MappingTarget Phase phase, PhaseRequest request);

    PhaseSummaryResponse toSummaryResponse(Phase phase);

    default double roundToTwoDecimal(Double value) {
        return value == null ? 0.0 : Math.round(value * 100.0) / 100.0;
    }
}