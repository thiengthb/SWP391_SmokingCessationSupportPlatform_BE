package com.swpteam.smokingcessation.controller.v1.tracking;

import com.swpteam.smokingcessation.domain.dto.plan.PlanRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPlanService;
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

    @GetMapping("/my-current-plan")
    ResponseEntity<ApiResponse<PlanResponse>> getPlanById(@PathVariable String id) {
        PlanResponse response = planService.getMyCurrentPlan();
        return ResponseEntity.ok(
                ApiResponse.<PlanResponse>builder()
                        .code(SuccessCode.PLAN_GET_BY_ID.getCode())
                        .message(SuccessCode.PLAN_GET_BY_ID.getMessage())
                        .result(response)
                        .build()
        );
    }

    //template
    @GetMapping("/template")
    //template
    public ResponseEntity<ApiResponse<PlanResponse>> getPlanTemplate(@RequestParam int ftndScore) {
        return ResponseEntity.ok(
                ApiResponse.<PlanResponse>builder()
                        .code(SuccessCode.PLAN_TEMPLATE_GET.getCode())
                        .message(SuccessCode.PLAN_TEMPLATE_GET.getMessage())
                        .result(planService.generatePlanByFtndScore(ftndScore))
                        .build()
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<PlanResponse>> createPlan(@Valid @RequestBody PlanRequest request) {
        PlanResponse response = planService.createPlan(request);
        return ResponseEntity.ok(
                ApiResponse.<PlanResponse>builder()
                        .code(SuccessCode.PLAN_CREATED.getCode())
                        .message(SuccessCode.PLAN_CREATED.getMessage())
                        .result(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<PlanResponse>> updatePlanById(@PathVariable String id, @Valid @RequestBody PlanRequest request) {
        PlanResponse response = planService.updatePlanById(id, request);
        return ResponseEntity.ok(
                ApiResponse.<PlanResponse>builder()
                        .code(SuccessCode.PLAN_UPDATED.getCode())
                        .message(SuccessCode.PLAN_UPDATED.getMessage())
                        .result(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deletePlanById(@PathVariable String id) {
        planService.softDeletePlanById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(SuccessCode.PLAN_DELETED.getCode())
                        .message(SuccessCode.PLAN_DELETED.getMessage())
                        .build()
        );
    }
}
