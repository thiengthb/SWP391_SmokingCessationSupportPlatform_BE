package com.swpteam.smokingcessation.feature.api.v1.tracking;

import com.swpteam.smokingcessation.feature.service.impl.tracking.PhaseServiceImpl;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseRequest;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/phases")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PhaseController {
    PhaseServiceImpl phaseServiceImpl;

    @GetMapping
    ResponseEntity<ApiResponse<Page<PhaseResponse>>> getPhasePage(@Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<PhaseResponse>>builder()
                        .code(SuccessCode.PHASE_GET_ALL.getCode())
                        .message(SuccessCode.PHASE_GET_ALL.getMessage())
                        .result(phaseServiceImpl.getPhasePage(request))
                        .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<PhaseResponse>> getPhaseById(@PathVariable String id) {
        PhaseResponse response = phaseServiceImpl.getPhaseById(id);
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
        PhaseResponse response = phaseServiceImpl.createPhase(request);
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
        PhaseResponse response = phaseServiceImpl.updatePhaseById(id, request);
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
        phaseServiceImpl.softDeletePhaseById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(SuccessCode.PHASE_DELETED.getCode())
                        .message(SuccessCode.PHASE_DELETED.getMessage())
                        .build()
        );
    }
}