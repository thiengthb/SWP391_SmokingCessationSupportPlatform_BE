package com.swpteam.smokingcessation.service.interfaces.tracking;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.domain.entity.Plan;
import org.springframework.data.domain.Page;

public interface IPlanService {

    Page<PlanResponse> getMyPlanPage(PageableRequest request);

    PlanResponse getPlanById(String id);

    PlanResponse getMyCurrentPlan();

    PlanResponse createPlan(PlanRequest request);

    PlanResponse updatePlanById(String id, PlanRequest request);

    PlanResponse generatePlanByFtndScore(int ftndScore);

    Plan findPlanByIdOrThrowError(String id);

    void softDeletePlanById(String id);
}