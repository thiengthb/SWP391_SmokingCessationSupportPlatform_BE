package com.swpteam.smokingcessation.controller.v1.tracking;

import com.swpteam.smokingcessation.domain.dto.record.RecordCreateRequest;
import com.swpteam.smokingcessation.domain.dto.record.RecordResponse;
import com.swpteam.smokingcessation.domain.dto.record.RecordUpdateRequest;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.service.interfaces.tracking.IRecordService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RecordController {

    IRecordService IRecordService;

    @GetMapping
    ResponseEntity<ApiResponse<Page<RecordResponse>>> getRecordPage(@Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<RecordResponse>>builder()
                        .code(SuccessCode.RECORD_GET_ALL.getCode())
                        .message(SuccessCode.RECORD_GET_ALL.getMessage())
                        .result(IRecordService.getRecordPage(request))
                        .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<RecordResponse>> getRecordById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.<RecordResponse>builder()
                        .code(SuccessCode.RECORD_GET_BY_ID.getCode())
                        .message(SuccessCode.RECORD_GET_BY_ID.getMessage())
                        .result(IRecordService.getRecordById(id))
                        .build()
        );
    }

    @GetMapping("/account/{id}")
    ResponseEntity<ApiResponse<Page<RecordResponse>>> getRecordPageByAccountId(@PathVariable String id, @Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<RecordResponse>>builder()
                        .code(SuccessCode.RECORD_GET_BY_ACCOUNT.getCode())
                        .message(SuccessCode.RECORD_GET_BY_ACCOUNT.getMessage())
                        .result(IRecordService.getRecordPageByAccountId(id, request))
                        .build()
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<RecordResponse>> createRecord(@RequestBody @Valid RecordCreateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<RecordResponse>builder()
                        .code(SuccessCode.RECORD_CREATED.getCode())
                        .message(SuccessCode.RECORD_CREATED.getMessage())
                        .result(IRecordService.createRecord(request))
                        .build()
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<RecordResponse>> updateRecord(@PathVariable String id, @RequestBody @Valid RecordUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<RecordResponse>builder()
                        .code(SuccessCode.RECORD_UPDATED.getCode())
                        .message(SuccessCode.RECORD_UPDATED.getMessage())
                        .result(IRecordService.updateRecord(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> delete(@PathVariable String id) {
        IRecordService.softDeleteRecordById(id);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .code(SuccessCode.RECORD_DELETED.getCode())
                        .message(SuccessCode.RECORD_DELETED.getMessage())
                        .build()
        );
    }
}