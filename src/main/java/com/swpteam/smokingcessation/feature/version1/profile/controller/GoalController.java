package com.swpteam.smokingcessation.feature.version1.profile.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.goal.*;
import com.swpteam.smokingcessation.feature.version1.profile.service.IGoalProgressService;
import com.swpteam.smokingcessation.feature.version1.profile.service.IGoalService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Goal", description = "Manage goal catalog")
public class GoalController {

    IGoalService goalService;
    IGoalProgressService goalProgressService;
    ResponseUtilService responseUtilService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GoalListItemResponse>>> getGoals(
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.GOAL_PAGE_FETCHED,
                goalService.getPublicGoals()
        );
    }

    @GetMapping("/my-goals")
    public ResponseEntity<ApiResponse<List<GoalListItemResponse>>> getMyGoals() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.GOAL_PAGE_FETCHED,
                goalService.getMyGoals()
        );
    }

    @GetMapping("/goal-details/{id}")
    public ResponseEntity<ApiResponse<GoalDetailsResponse>> getGoalDetailsById(@PathVariable String id) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.GOAL_PAGE_FETCHED,
                goalService.getMyGoalDetailsById(id)
        );
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<GoalDetailsResponse>> getGoalByName(
            @PathVariable String name
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.GOAL_FETCHED_BY_NAME,
                goalService.getGoalByName(name)
        );
    }

    @GetMapping("/hall-of-fame")
    public ResponseEntity<ApiResponse<List<HallOfFameResponse>>> getHallOfFame(
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.GOAL_PAGE_FETCHED,
                goalProgressService.getHallOfFame()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GoalDetailsResponse>> createGoal(
            @RequestBody @Valid GoalCreateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.GOAL_CREATED,
                goalService.createGoal(request)
        );
    }

    @PutMapping("/{name}")
    public ResponseEntity<ApiResponse<GoalDetailsResponse>> updateGoal(
            @PathVariable String name,
            @RequestBody @Valid GoalUpdateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.GOAL_UPDATED,
                goalService.updateGoal(name, request)
        );
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(
            @PathVariable String name
    ) {
        goalService.softDeleteGoal(name);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.GOAL_DELETED
        );
    }

}
