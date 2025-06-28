package com.swpteam.smokingcessation.controller.v1.tracking;

import com.swpteam.smokingcessation.domain.dto.plan.PlanRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPlanService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    
    @GetMapping("/template")
    public ResponseEntity<ApiResponse<PlanResponse>> getPlanTemplate(
            @RequestParam int ftndScore
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PLAN_TEMPLATE_FETCHED,
                planService.generatePlanByFtndScore(ftndScore)
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

}
