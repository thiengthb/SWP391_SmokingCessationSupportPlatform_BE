package com.swpteam.smokingcessation.feature.version1.tracking.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.counter.CounterResponse;
import com.swpteam.smokingcessation.feature.version1.tracking.service.ICounterService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/counters")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Counter", description = "Manage counter-related operations")
public class CounterController {

    ICounterService counterService;
    ResponseUtilService responseUtilService;

    @PutMapping("/start")
    ResponseEntity<ApiResponse<CounterResponse>> startOrResetCounter() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.COUNTER_STARTED,
                counterService.startCounter()
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<CounterResponse>> getCounter() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.COUNTER_FETCHED,
                counterService.getCounter()
        );
    }
}
