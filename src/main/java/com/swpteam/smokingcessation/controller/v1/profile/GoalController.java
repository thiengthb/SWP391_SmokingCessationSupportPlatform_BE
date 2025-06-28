package com.swpteam.smokingcessation.controller.v1.profile;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.goal.GoalCreateRequest;
import com.swpteam.smokingcessation.domain.dto.goal.GoalDetailsResponse;
import com.swpteam.smokingcessation.domain.dto.goal.GoalListItemResponse;
import com.swpteam.smokingcessation.domain.dto.goal.GoalUpdateRequest;
import com.swpteam.smokingcessation.service.interfaces.profile.IGoalService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<GoalListItemResponse>>> getGoals(
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.GOAL_GET_PUBLIC_GOALS,
                goalService.getPublicGoals()
        );
    }

    @GetMapping("/my-goals")
    public ResponseEntity<ApiResponse<List<GoalListItemResponse>>> getMyGoals() {
        return ResponseUtil.buildResponse(
                SuccessCode.GOAL_GET_PERSONAL_GOALS,
                goalService.getMyGoals()
        );
    }

    @GetMapping("/goal-details/{id}")
    public ResponseEntity<ApiResponse<GoalDetailsResponse>> getGoalDetailsById(@PathVariable String id) {
        return ResponseUtil.buildResponse(
                SuccessCode.GOAL_GET_DETAILS,
                goalService.getMyGoalDetailsById(id)
        );
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<GoalDetailsResponse>> getGoalByName(
            @PathVariable String name
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.GOAL_GET_BY_NAME,
                goalService.getGoalByName(name)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GoalDetailsResponse>> createGoal(
            @RequestBody @Valid GoalCreateRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.GOAL_CREATED,
                goalService.createGoal(request)
        );
    }

    @PutMapping("/{name}")
    public ResponseEntity<ApiResponse<GoalDetailsResponse>> updateGoal(
            @PathVariable String name,
            @RequestBody @Valid GoalUpdateRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.GOAL_UPDATED,
                goalService.updateGoal(name, request)
        );
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<String>> deleteGoal(
            @PathVariable String name
    ) {
        goalService.softDeleteGoal(name);
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.GOAL_DELETED,
                null
        );
    }

}
