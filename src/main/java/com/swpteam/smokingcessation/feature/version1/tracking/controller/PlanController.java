package com.swpteam.smokingcessation.feature.version1.tracking.controller;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanPageResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.plan.PlanSummaryResponse;
import com.swpteam.smokingcessation.feature.version1.tracking.service.IPlanService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Plan", description = "Manage plan-related operations")
public class PlanController {

    IPlanService planService;
    ResponseUtilService responseUtilService;

    @GetMapping("/my-current-plan")
    ResponseEntity<ApiResponse<PlanResponse>> getPlanById() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PLAN_FETCHED_BY_ID,
                planService.getMyCurrentPlan()
        );
    }

    @GetMapping("/my-plans")
    ResponseEntity<ApiResponse<PageResponse<PlanPageResponse>>> getMyPlanPage(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PLAN_PAGE_FETCHED,
                planService.getMyPlanPage(request)
        );
    }

    @GetMapping("/template")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getPlanTemplate() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PLAN_TEMPLATE_FETCHED,
                planService.generateAllPlans()
        );
    }


    @PostMapping
    ResponseEntity<ApiResponse<PlanResponse>> createPlan(
            @Valid @RequestBody PlanRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PLAN_CREATED,
                planService.createPlan(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<PlanResponse>> updatePlanById(
            @PathVariable String id,
            @Valid @RequestBody PlanRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PLAN_UPDATED,
                planService.updatePlanById(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deletePlanById(
            @PathVariable String id
    ) {
        planService.softDeletePlanById(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PLAN_DELETED
        );
    }

    @GetMapping("/summary/{planId}")
    ResponseEntity<ApiResponse<PlanSummaryResponse>> getPlanSummary(
            @PathVariable String planId
    ) {
        PlanSummaryResponse summary = planService.getPlanSummary(planId);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PLAN_FETCHED_BY_ID,
                summary
        );
    }

}
