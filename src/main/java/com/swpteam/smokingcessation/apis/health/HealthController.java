package com.swpteam.smokingcessation.apis.health;

import com.swpteam.smokingcessation.apis.health.dto.HealthRequest;
import com.swpteam.smokingcessation.apis.health.dto.HealthResponse;
import com.swpteam.smokingcessation.apis.health.dto.HealthUpdate;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constants.ErrorCode;
import com.swpteam.smokingcessation.constants.SuccessCode;
import com.swpteam.smokingcessation.exception.AppException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class HealthController {

    HealthService healthService;
    HealthRepository healthRepository;

    @PostMapping
    ResponseEntity<ApiResponse<HealthResponse>> createHealth(@RequestBody @Valid HealthRequest request) {
        if (!healthRepository.existsByAccountIdAndIsDeletedFalse(request.getAccountId())) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_EXISTED);
        }

        return ResponseEntity.ok(
                ApiResponse.<HealthResponse>builder()
                        .code(SuccessCode.HEALTH_CREATED.getCode())
                        .message(SuccessCode.HEALTH_CREATED.getMessage())
                        .result(healthService.createHealth(request))
                        .build()
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<Iterable<HealthResponse>>> getHealthPage(@Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Iterable<HealthResponse>>builder()
                        .result(healthService.getHealthPage(request))
                        .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<HealthResponse>> getHealthById(@PathVariable String id) {
        if (!healthRepository.existsByAccountIdAndIsDeletedFalse(id)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return ResponseEntity.ok(
                ApiResponse.<HealthResponse>builder()
                        .result(healthService.getHealthById(id))
                        .build()
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<HealthResponse>> updateHealth(@PathVariable String id, @RequestBody @Valid HealthUpdate request) {
        if (!healthRepository.existsByAccountIdAndIsDeletedFalse(id)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return ResponseEntity.ok(
                ApiResponse.<HealthResponse>builder()
                        .code(SuccessCode.HEALTH_UPDATED.getCode())
                        .message(SuccessCode.HEALTH_UPDATED.getMessage())
                        .result(healthService.updateHealth(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> deleteHealthById(@PathVariable String id) {
        if (!healthRepository.existsByAccountIdAndIsDeletedFalse(id)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        healthService.softDeleteHealthById(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .code(SuccessCode.HEALTH_DELETED.getCode())
                        .message(SuccessCode.HEALTH_DELETED.getMessage())
                        .build()
        );
    }
}