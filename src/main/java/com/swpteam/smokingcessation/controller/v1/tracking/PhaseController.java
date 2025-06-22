package com.swpteam.smokingcessation.controller.v1.tracking;

import com.swpteam.smokingcessation.domain.dto.phase.PhaseRequest;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPhaseService;
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
@RequestMapping("/api/v1/phases")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Phase", description = "Manage phase-related operations")
public class PhaseController {

    IPhaseService phaseService;

    @GetMapping("/plan/{id}")
    ResponseEntity<ApiResponse<List<PhaseResponse>>> getPhasePage(@PathVariable String planId) {
        return ResponseEntity.ok(
                ApiResponse.<List<PhaseResponse>>builder()
                        .code(SuccessCode.PHASE_GET_ALL.getCode())
                        .message(SuccessCode.PHASE_GET_ALL.getMessage())
                        .result(phaseService.getPhaseListByPlanId(planId))
                        .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<PhaseResponse>> getPhaseById(@PathVariable String id) {
        PhaseResponse response = phaseService.getPhaseById(id);
        return ResponseEntity.ok(
                ApiResponse.<PhaseResponse>builder()
                        .code(SuccessCode.PHASE_GET_BY_ID.getCode())
                        .message(SuccessCode.PHASE_GET_BY_ID.getMessage())
                        .result(response)
                        .build()
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<PhaseResponse>> createPhase(@Valid @RequestBody PhaseRequest request) {
        PhaseResponse response = phaseService.createPhase(request);
        return ResponseEntity.ok(
                ApiResponse.<PhaseResponse>builder()
                        .code(SuccessCode.PHASE_CREATED.getCode())
                        .message(SuccessCode.PHASE_CREATED.getMessage())
                        .result(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<PhaseResponse>> updatePhaseById(
            @PathVariable String id,
            @Valid @RequestBody PhaseRequest request) {
        PhaseResponse response = phaseService.updatePhaseById(id, request);
        return ResponseEntity.ok(
                ApiResponse.<PhaseResponse>builder()
                        .code(SuccessCode.PHASE_UPDATED.getCode())
                        .message(SuccessCode.PHASE_UPDATED.getMessage())
                        .result(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deletePhaseById(@PathVariable String id) {
        phaseService.softDeletePhaseById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(SuccessCode.PHASE_DELETED.getCode())
                        .message(SuccessCode.PHASE_DELETED.getMessage())
                        .build()
        );
    }
}