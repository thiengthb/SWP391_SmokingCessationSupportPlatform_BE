package com.swpteam.smokingcessation.feature.service.interfaces.tracking;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import org.springframework.data.domain.Page;

public interface PlanService {
    Page<PlanResponse> getPlanPage(PageableRequest request);

    PlanResponse getPlanById(String id);

    PlanResponse createPlan(PlanRequest request);

    PlanResponse updatePlanById(String id, PlanRequest request);

    void softDeletePlanById(String id);
}