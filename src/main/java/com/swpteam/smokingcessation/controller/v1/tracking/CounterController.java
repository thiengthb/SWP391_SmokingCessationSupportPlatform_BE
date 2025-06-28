package com.swpteam.smokingcessation.controller.v1.tracking;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.counter.CounterResponse;
import com.swpteam.smokingcessation.service.interfaces.tracking.ICounterService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
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

    @PutMapping("/start")
    ResponseEntity<ApiResponse<CounterResponse>> startOrResetCounter() {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COUNTER_START,
                counterService.startCounter()
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<CounterResponse>> getCounter() {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COUNTER_GET,
                counterService.getCounter()
        );
    }
}
