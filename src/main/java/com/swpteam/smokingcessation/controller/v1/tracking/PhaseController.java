package com.swpteam.smokingcessation.controller.v1.tracking;

import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseSummaryResponse;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPhaseService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/phases")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Phase", description = "Manage phase-related operations")
public class PhaseController {

    IPhaseService phaseService;
    ResponseUtilService responseUtilService;

    @GetMapping("/plan/{id}")
    ResponseEntity<ApiResponse<List<PhaseResponse>>> getPhaseList(
            @PathVariable String planId
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PHASE_LIST_FETCHED,
                phaseService.getPhaseListByPlanId(planId)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<PhaseResponse>> getPhaseById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PHASE_FETCHED_BY_ID,
                phaseService.getPhaseById(id)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deletePhaseById(@PathVariable String id) {
        phaseService.softDeletePhaseById(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PHASE_DELETED
        );
    }

    @GetMapping("/plan/{planId}/completed")
    ResponseEntity<ApiResponse<List<PhaseSummaryResponse>>> getCompletedPhaseSummaries(
            @PathVariable String planId
    ) {
        List<PhaseSummaryResponse> summaries = phaseService.getCompletedPhaseSummaries(planId);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PHASE_LIST_FETCHED,
                summaries
        );
    }


}