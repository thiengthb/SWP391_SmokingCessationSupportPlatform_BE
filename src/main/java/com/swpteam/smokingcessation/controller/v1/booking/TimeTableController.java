package com.swpteam.smokingcessation.controller.v1.booking;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableRequest;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableResponse;
import com.swpteam.smokingcessation.service.interfaces.booking.ITimeTableService;
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
@RequestMapping("/api/v1/timetables")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "TimeTable", description = "Manage timetable-related operations")
public class TimeTableController {

    ITimeTableService timeTableService;

    @GetMapping
    ResponseEntity<ApiResponse<Page<TimeTableResponse>>> getTimeTablePage(@Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<TimeTableResponse>>builder()
                        .code(SuccessCode.TIMETABLE_GET_ALL.getCode())
                        .message(SuccessCode.TIMETABLE_GET_ALL.getMessage())
                        .result(timeTableService.getTimeTablePage(request))
                        .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<TimeTableResponse>> getTimeTableById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.<TimeTableResponse>builder()
                        .code(SuccessCode.TIMETABLE_GET_BY_ID.getCode())
                        .message(SuccessCode.TIMETABLE_GET_BY_ID.getMessage())
                        .result(timeTableService.getTimeTableById(id))
                        .build()
        );
    }

    @GetMapping("/coach/{coachId}")
    ResponseEntity<ApiResponse<Page<TimeTableResponse>>> getTimeTablesByCoachId(
            @PathVariable String coachId,
            @Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<TimeTableResponse>>builder()
                        .code(SuccessCode.TIMETABLE_GET_ALL.getCode())
                        .message(SuccessCode.TIMETABLE_GET_ALL.getMessage())
                        .result(timeTableService.getTimeTablesByCoachId(coachId, request))
                        .build()
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<TimeTableResponse>> createTimeTable(@Valid @RequestBody TimeTableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<TimeTableResponse>builder()
                        .code(SuccessCode.TIMETABLE_CREATED.getCode())
                        .message(SuccessCode.TIMETABLE_CREATED.getMessage())
                        .result(timeTableService.createTimeTable(request))
                        .build()
        );
    }
}