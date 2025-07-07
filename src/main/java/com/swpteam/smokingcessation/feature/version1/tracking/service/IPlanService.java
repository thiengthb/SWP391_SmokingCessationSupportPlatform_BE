package com.swpteam.smokingcessation.feature.version1.tracking.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanPageResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Plan;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;

import java.util.List;

public interface IPlanService {

    PageResponse<PlanPageResponse> getMyPlanPage(PageableRequest request);

    PlanResponse getPlanById(String id);

    PlanResponse getMyCurrentPlan();

    PlanResponse createPlan(PlanRequest request);

    PlanResponse updatePlanById(String id, PlanRequest request);

    Plan findPlanByIdOrThrowError(String id);

    void softDeletePlanById(String id);

    Plan findByAccountIdAndPlanStatusAndIsDeletedFalse(String accountId, PlanStatus planStatus);

    void dailyCheckingPlanStatus();

    void updateCompletedPlan(Plan plan, double successRate, PlanStatus planStatus);

    List<Plan> getAllActivePlans();

    PlanSummaryResponse getPlanSummary(String planId);
}