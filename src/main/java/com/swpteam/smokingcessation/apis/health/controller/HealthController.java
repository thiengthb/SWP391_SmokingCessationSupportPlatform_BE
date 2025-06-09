package com.swpteam.smokingcessation.apis.health.controller;

import com.swpteam.smokingcessation.apis.health.DTO.request.HealthRequest;
import com.swpteam.smokingcessation.apis.health.DTO.request.HealthUpdate;
import com.swpteam.smokingcessation.apis.health.service.HealthService;
import com.swpteam.smokingcessation.common.response.ApiResponse;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HealthController {
    HealthService healthService;

    @PostMapping
    ResponseEntity<ApiResponse> create(
            @RequestBody @Valid HealthRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currentUserEmail = userDetails.getUsername();
        String requestAccountId = request.getAccountId();

        if (!healthService.isAccountOwnedByUser(requestAccountId, currentUserEmail)) {
            throw new AppException(ErrorCode.INVALID_ACCOUNT_ID);
        }

        return ResponseEntity.ok(ApiResponse.builder()
                .result(healthService.create(request))
                .build());
    }

    @GetMapping
    ResponseEntity<ApiResponse> getAll(@AuthenticationPrincipal UserDetails userDetails) {
        String currentUserEmail = userDetails.getUsername();

        return ResponseEntity.ok(ApiResponse.builder()
                .result(healthService.getAllByUserEmail(currentUserEmail))
                .build());
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currentUserEmail = userDetails.getUsername();


        if (!healthService.isHealthOwnedByUser(id, currentUserEmail)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return ResponseEntity.ok(ApiResponse.builder()
                .result(healthService.getById(id))
                .build());
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid HealthUpdate request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currentUserEmail = userDetails.getUsername();

        if (!healthService.isHealthOwnedByUser(id, currentUserEmail)) {
            throw new AppException(ErrorCode.INVALID_ACCOUNT_ID);
        }

        return ResponseEntity.ok(ApiResponse.builder()
                .result(healthService.update(id, request))
                .build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) { // ✅ THÊM

        String currentUserEmail = userDetails.getUsername();

        if (!healthService.isHealthOwnedByUser(id, currentUserEmail)) {
            throw new AppException(ErrorCode.INVALID_ACCOUNT_ID);
        }

        healthService.delete(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .result("Health record deleted")
                .build());
    }
}