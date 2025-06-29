package com.swpteam.smokingcessation.service.interfaces.tracking;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Plan;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IPlanService {

    PageResponse<PlanResponse> getMyPlanPage(PageableRequest request);

    PlanResponse getPlanById(String id);

    PlanResponse getMyCurrentPlan();

    PlanResponse createPlan(PlanRequest request);

    PlanResponse updatePlanById(String id, PlanRequest request);

    List<PlanResponse> generateAllPlans();

    Plan findPlanByIdOrThrowError(String id);

    void softDeletePlanById(String id);

    Plan findByAccountIdAndPlanStatusAndIsDeletedFalse(String accountId, PlanStatus planStatus);

    void dailyCheckingPlanStatus();

    void updateCompletedPlan(Plan plan, double successRate, PlanStatus planStatus);

    List<Plan> getAllActivePlans();
}