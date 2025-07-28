package com.swpteam.smokingcessation.feature.version1.tracking.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.record.RecordHabitRequest;
import com.swpteam.smokingcessation.domain.dto.record.RecordHabitResponse;
import com.swpteam.smokingcessation.feature.version1.tracking.service.IRecordHabitService;
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
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Record", description = "Manage record-related operations")
public class RecordHabitController {

    IRecordHabitService recordService;
    ResponseUtilService responseUtilService;

    @GetMapping("/my-records")
    ResponseEntity<ApiResponse<PageResponse<RecordHabitResponse>>> getMyRecordPage(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.REVIEW_PAGE_FETCHED,
                recordService.getMyRecordPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<RecordHabitResponse>> getRecordById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.RECORD_FETCHED_BY_ID,
                recordService.getRecordById(id)
        );
    }

    @GetMapping("/account/{id}")
    ResponseEntity<ApiResponse<PageResponse<RecordHabitResponse>>> getRecordPageByAccountId(
            @PathVariable String id,
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.RECORD_FETCHED_BY_ACCOUNT,
                recordService.getRecordPageByAccountId(id, request)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<RecordHabitResponse>> createRecord(
            @RequestBody @Valid RecordHabitRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.RECORD_CREATED,
                recordService.createRecord(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<RecordHabitResponse>> updateRecord(
            @PathVariable String id,
            @RequestBody @Valid RecordHabitRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.RECORD_UPDATED,
                recordService.updateRecord(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String id
    ) {
        recordService.softDeleteRecordById(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.RECORD_DELETED
        );
    }

}