package com.swpteam.smokingcessation.controller.v1.profile;

import com.swpteam.smokingcessation.domain.dto.health.HealthCreateRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthResponse;
import com.swpteam.smokingcessation.domain.dto.health.HealthUpdateRequest;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.service.interfaces.profile.IHealthService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/healths")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Health", description = "Manage health-related operations")
public class HealthController {

    IHealthService healthService;

    @GetMapping
    ResponseEntity<ApiResponse<Page<HealthResponse>>> getHealthPage(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.HEALTH_GET_ALL,
                healthService.getHealthPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<HealthResponse>> getHealthById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.HEALTH_GET_BY_ID,
                healthService.getHealthById(id)
        );
    }

    @GetMapping("/account/{id}")
    ResponseEntity<ApiResponse<Page<HealthResponse>>> getHealthPageByAccountId(
            @PathVariable String id,
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.HEALTH_GET_BY_ACCOUNT,
                healthService.getHealthPageByAccountId(id, request)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<HealthResponse>> createHealth(
            @RequestBody @Valid HealthCreateRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.HEALTH_CREATED,
                healthService.createHealth(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<HealthResponse>> updateHealth(
            @PathVariable String id,
            @RequestBody @Valid HealthUpdateRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.HEALTH_UPDATED,
                healthService.updateHealth(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> deleteHealthById(
            @PathVariable String id
    ) {
        healthService.softDeleteHealthById(id);
        return ResponseUtil.buildResponse(
                SuccessCode.HEALTH_DELETED,
                null
        );
    }
}