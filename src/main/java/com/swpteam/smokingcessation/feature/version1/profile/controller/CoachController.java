package com.swpteam.smokingcessation.feature.version1.profile.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.coach.CoachCreateRequest;
import com.swpteam.smokingcessation.domain.dto.coach.CoachResponse;
import com.swpteam.smokingcessation.domain.dto.coach.CoachUpdateRequest;
import com.swpteam.smokingcessation.feature.version1.profile.service.ICoachService;
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
@RequestMapping("/api/v1/coaches")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Coach", description = "Manage coach-related operations")
public class CoachController {

    ICoachService coachService;
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<CoachResponse>>> getCoachPage(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.COACH_PAGE_FETCHED,
                coachService.getCoachPage(request)
        );
    }

    @GetMapping("/search")
    ResponseEntity<ApiResponse<PageResponse<CoachResponse>>> searchCoachesByName(
            @RequestParam String name,
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.COACH_PAGE_FETCHED,
                coachService.searchCoachesByName(name, request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<CoachResponse>> getCoachById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.COACH_FETCHED_BY_ID,
                coachService.getCoachById(id)
        );
    }

    @GetMapping("/my-profile")
    ResponseEntity<ApiResponse<CoachResponse>> getMyCoachProfile() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.COACH_FETCHED_BY_ID,
                coachService.getMyCoachProfile()
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<CoachResponse>> createCoach(
            @Valid @RequestBody CoachCreateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.COACH_CREATED,
                coachService.registerCoachProfile(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<CoachResponse>> updateCoachById(
            @PathVariable String id,
            @Valid @RequestBody CoachUpdateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.COACH_UPDATED,
                coachService.updateCoachById(id, request)
        );
    }

    @PutMapping("/my-profile")
    ResponseEntity<ApiResponse<CoachResponse>> updateMyCoachProfile(
            @Valid @RequestBody CoachUpdateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.COACH_UPDATED,
                coachService.updateMyCoachProfile(request)
        );
    }

}