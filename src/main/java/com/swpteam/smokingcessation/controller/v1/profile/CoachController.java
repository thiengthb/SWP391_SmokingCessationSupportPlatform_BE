package com.swpteam.smokingcessation.controller.v1.profile;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.coach.CoachRequest;
import com.swpteam.smokingcessation.domain.dto.coach.CoachResponse;
import com.swpteam.smokingcessation.service.interfaces.profile.ICoachService;
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
@RequestMapping("/api/v1/coaches")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Coach", description = "Manage coach-related operations")
public class CoachController {
    ICoachService coachService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<CoachResponse>>> getCoachPage(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COACH_GET_ALL,
                coachService.getCoachPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<CoachResponse>> getCoachById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COACH_GET_BY_ID,
                coachService.getCoachById(id)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<CoachResponse>> createCoach(
            @Valid @RequestBody CoachRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COACH_CREATED,
                coachService.registerCoachProfile(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<CoachResponse>> updateCoachById(
            @PathVariable String id,
            @Valid @RequestBody CoachRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COACH_UPDATED,
                coachService.updateCoachById(id, request)
        );
    }

}