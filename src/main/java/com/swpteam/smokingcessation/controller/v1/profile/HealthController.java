package com.swpteam.smokingcessation.controller.v1.profile;

import com.swpteam.smokingcessation.domain.dto.health.HealthCreateRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthResponse;
import com.swpteam.smokingcessation.domain.dto.health.HealthUpdateRequest;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.service.interfaces.profile.IHealthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/healths")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class HealthController {

    IHealthService IHealthService;

    @GetMapping
    ResponseEntity<ApiResponse<Page<HealthResponse>>> getHealthPage(@Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<HealthResponse>>builder()
                        .code(SuccessCode.HEALTH_GET_ALL.getCode())
                        .message(SuccessCode.HEALTH_GET_ALL.getMessage())
                        .result(IHealthService.getHealthPage(request))
                        .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<HealthResponse>> getHealthById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.<HealthResponse>builder()
                        .code(SuccessCode.HEALTH_GET_BY_ID.getCode())
                        .message(SuccessCode.HEALTH_GET_BY_ID.getMessage())
                        .result(IHealthService.getHealthById(id))
                        .build()
        );
    }

    @GetMapping("/account/{id}")
    ResponseEntity<ApiResponse<Page<HealthResponse>>> getHealthPageByAccountId(@PathVariable String id, @Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<HealthResponse>>builder()
                        .code(SuccessCode.HEALTH_GET_BY_ACCOUNT.getCode())
                        .message(SuccessCode.HEALTH_GET_BY_ACCOUNT.getMessage())
                        .result(IHealthService.getHealthPageByAccountId(id, request))
                        .build()
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<HealthResponse>> createHealth(@RequestBody @Valid HealthCreateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<HealthResponse>builder()
                        .code(SuccessCode.HEALTH_CREATED.getCode())
                        .message(SuccessCode.HEALTH_CREATED.getMessage())
                        .result(IHealthService.createHealth(request))
                        .build()
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<HealthResponse>> updateHealth(@PathVariable String id, @RequestBody @Valid HealthUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<HealthResponse>builder()
                        .code(SuccessCode.HEALTH_UPDATED.getCode())
                        .message(SuccessCode.HEALTH_UPDATED.getMessage())
                        .result(IHealthService.updateHealth(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> deleteHealthById(@PathVariable String id) {
        IHealthService.softDeleteHealthById(id);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .code(SuccessCode.HEALTH_DELETED.getCode())
                        .message(SuccessCode.HEALTH_DELETED.getMessage())
                        .build()
        );
    }
}