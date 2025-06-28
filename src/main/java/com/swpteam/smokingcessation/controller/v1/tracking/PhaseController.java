package com.swpteam.smokingcessation.controller.v1.tracking;

import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPhaseService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
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

    @GetMapping("/plan/{id}")
    ResponseEntity<ApiResponse<List<PhaseResponse>>> getPhasePage(
            @PathVariable String planId
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.PHASE_GET_ALL,
                phaseService.getPhaseListByPlanId(planId)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<PhaseResponse>> getPhaseById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.PHASE_GET_BY_ID,
                phaseService.getPhaseById(id)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deletePhaseById(@PathVariable String id) {
        phaseService.softDeletePhaseById(id);
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.PHASE_DELETED
        );
    }

}