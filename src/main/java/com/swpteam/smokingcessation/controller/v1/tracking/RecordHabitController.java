package com.swpteam.smokingcessation.controller.v1.tracking;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.record.RecordHabitCreateRequest;
import com.swpteam.smokingcessation.domain.dto.record.RecordHabitResponse;
import com.swpteam.smokingcessation.domain.dto.record.RecordHabitUpdateRequest;
import com.swpteam.smokingcessation.service.interfaces.tracking.IRecordHabitService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
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

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<RecordHabitResponse>>> getMyRecordPage(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.RECORD_GET_ALL,
                recordService.getMyRecordPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<RecordHabitResponse>> getRecordById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.RECORD_GET_BY_ID,
                recordService.getRecordById(id)
        );
    }

    @GetMapping("/account/{id}")
    ResponseEntity<ApiResponse<PageResponse<RecordHabitResponse>>> getRecordPageByAccountId(
            @PathVariable String id,
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.RECORD_GET_BY_ACCOUNT,
                recordService.getRecordPageByAccountId(id, request)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<RecordHabitResponse>> createRecord(
            @RequestBody @Valid RecordHabitCreateRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.RECORD_CREATED,
                recordService.createRecord(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<RecordHabitResponse>> updateRecord(
            @PathVariable String id,
            @RequestBody @Valid RecordHabitUpdateRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.RECORD_UPDATED,
                recordService.updateRecord(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> delete(
            @PathVariable String id
    ) {
        recordService.softDeleteRecordById(id);
        return ResponseUtil.buildResponse(
                SuccessCode.RECORD_DELETED,
                null
        );
    }

}