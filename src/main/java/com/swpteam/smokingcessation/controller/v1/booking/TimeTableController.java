package com.swpteam.smokingcessation.controller.v1.booking;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableRequest;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableResponse;
import com.swpteam.smokingcessation.service.interfaces.booking.ITimeTableService;
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
@RequestMapping("/api/v1/timetables")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "TimeTable", description = "Manage timetable-related operations")
public class TimeTableController {

    ITimeTableService timeTableService;
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<TimeTableResponse>>> getTimeTablePage(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.TIMETABLE_PAGE_FETCHED,
                timeTableService.getTimeTablePage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<TimeTableResponse>> getTimeTableById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.TIMETABLE_FETCHED_BY_ID,
                timeTableService.getTimeTableById(id)
        );
    }

    @GetMapping("/coach/{coachId}")
    ResponseEntity<ApiResponse<PageResponse<TimeTableResponse>>> getTimeTablesByCoachId(
            @PathVariable String coachId, @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.TIMETABLE_PAGE_FETCHED,
                timeTableService.getTimeTablesByCoachId(coachId, request)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<TimeTableResponse>> createTimeTable(
            @Valid @RequestBody TimeTableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.TIMETABLE_CREATED,
                timeTableService.createTimeTable(request)
        );
    }
}