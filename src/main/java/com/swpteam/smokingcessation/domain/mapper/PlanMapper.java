package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.plan.PlanPageResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Plan;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = PhaseMapper.class)
public interface PlanMapper {
    @Mapping(source = "account.id", target = "accountId")
    PlanResponse toResponse(Plan plan);

    @Mapping(target = "phases", source = "phases")
    Plan toEntity(PlanRequest planRequest);

    @Mapping(target = "phases", source = "phases")
    void update(@MappingTarget Plan plan, PlanRequest request);

    PlanSummaryResponse toSummaryResponse(Plan plan);

    PlanPageResponse toPageResponse(Plan plan);
}
