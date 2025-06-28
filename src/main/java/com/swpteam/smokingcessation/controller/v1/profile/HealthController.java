package com.swpteam.smokingcessation.controller.v1.profile;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.domain.dto.health.HealthRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.service.interfaces.profile.IHealthService;
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
@RequestMapping("/api/v1/healths")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Health", description = "Manage health-related operations")
public class HealthController {

    IHealthService healthService;
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<HealthResponse>>> getHealthPage(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.HEALTH_PAGE_FETCHED,
                healthService.getHealthPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<HealthResponse>> getHealthById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.HEALTH_FETCHED_BY_ID,
                healthService.getHealthById(id)
        );
    }

    @GetMapping("/ftnd-status")
    ResponseEntity<ApiResponse<Boolean>> getMyFTNDStatus() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.HEALTH_FETCHED_BY_ACCOUNT,
                healthService.hasCompleteFTNDAssessment()
        );
    }

    @GetMapping("/account/{id}")
    ResponseEntity<ApiResponse<PageResponse<HealthResponse>>> getHealthPageByAccountId(
            @PathVariable String id,
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.HEALTH_FETCHED_BY_ACCOUNT,
                healthService.getHealthPageByAccountId(id, request)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<HealthResponse>> createHealth(
            @RequestBody @Valid HealthRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.HEALTH_CREATED,
                healthService.createHealth(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<HealthResponse>> updateHealth(
            @PathVariable String id,
            @RequestBody @Valid HealthRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.HEALTH_UPDATED,
                healthService.updateHealth(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteHealthById(
            @PathVariable String id
    ) {
        healthService.softDeleteHealthById(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.HEALTH_DELETED
        );
    }
}