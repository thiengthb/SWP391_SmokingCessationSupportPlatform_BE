package com.swpteam.smokingcessation.apis.record;

import com.swpteam.smokingcessation.apis.record.dto.RecordRequest;
import com.swpteam.smokingcessation.apis.record.dto.RecordResponse;
import com.swpteam.smokingcessation.apis.record.dto.RecordUpdate;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/record")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RecordController {
    RecordService recordService;

    @PostMapping
    ResponseEntity<ApiResponse<RecordResponse>> create(
            @RequestBody @Valid RecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currentUserEmail = userDetails.getUsername();
        String requestAccountId = request.getAccountId();

        if (!recordService.isAccountOwnedByUser(requestAccountId, currentUserEmail)) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_EXISTED);
        }

        return ResponseEntity.ok(
                ApiResponse.<RecordResponse>builder()
                        .code(SuccessCode.RECORD_CREATED.getCode())
                        .message(SuccessCode.RECORD_CREATED.getMessage())
                        .result(recordService.create(request))
                        .build()
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<Page<RecordResponse>>> getAll(
            @Valid PageableRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String currentUserEmail = userDetails.getUsername();

        return ResponseEntity.ok(
                ApiResponse.<Page<RecordResponse>>builder()
                        .result(recordService.getAllByUserEmail(request, currentUserEmail))
                        .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<RecordResponse>> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currentUserEmail = userDetails.getUsername();

        if (!recordService.isRecordOwnedByUser(id, currentUserEmail)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return ResponseEntity.ok(
                ApiResponse.<RecordResponse>builder()
                        .result(recordService.getById(id))
                        .build()
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<RecordResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid RecordUpdate request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currentUserEmail = userDetails.getUsername();

        if (!recordService.isRecordOwnedByUser(id, currentUserEmail)) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_EXISTED);
        }

        return ResponseEntity.ok(
                ApiResponse.<RecordResponse>builder()
                        .code(SuccessCode.RECORD_UPDATED.getCode())
                        .message(SuccessCode.RECORD_UPDATED.getMessage())
                        .result(recordService.update(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currentUserEmail = userDetails.getUsername();

        if (!recordService.isRecordOwnedByUser(id, currentUserEmail)) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_EXISTED);
        }

        recordService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .code(SuccessCode.RECORD_DELETED.getCode())
                        .message(SuccessCode.RECORD_DELETED.getMessage())
                        .build()
        );
    }
}