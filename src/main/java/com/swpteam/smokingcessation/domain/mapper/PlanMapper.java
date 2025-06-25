package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.plan.PlanRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Plan;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = PhaseMapper.class)
public interface PlanMapper {
    @Mapping(target = "successRate", expression = "java(roundToTwoDecimal(plan.getSuccessRate()))")
    @Mapping(source = "account.id", target = "accountId")
    PlanResponse toResponse(Plan plan);

    @Mapping(target = "phases", source = "phases")
    Plan toEntity(PlanRequest planRequest);

    @Mapping(target = "phases", source = "phases")
    void update(@MappingTarget Plan plan, PlanRequest request);

        /*
        @AfterMapping
        default void linkPhases(@MappingTarget Plan plan) {
            if (plan.getPhases() != null) {
                plan.getPhases().forEach(phase -> phase.setPlan(plan));
            }
        }
         */

    default double roundToTwoDecimal(Double value) {
        return value == null ? 0.0 : Math.round(value * 100.0) / 100.0;
    }
}
