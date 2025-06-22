package com.swpteam.smokingcessation.controller.v1.profile;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.goal.*;
import com.swpteam.smokingcessation.service.interfaces.profile.IGoalService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
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
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Goal", description = "Manage goal catalog")
public class GoalController {

    IGoalService goalService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GoalResponse>>> getGoalPage(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.GOAL_GET_ALL,
                goalService.getPublicGoalPage(request)
        );
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<GoalResponse>> getGoalByName(
            @PathVariable String name
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.GOAL_GET_BY_NAME,
                goalService.getGoalByName(name)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GoalResponse>> createGoal(
            @RequestBody @Valid GoalCreateRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.GOAL_CREATED,
                goalService.createGoal(request)
        );
    }

    @PutMapping("/{name}")
    public ResponseEntity<ApiResponse<GoalResponse>> updateGoal(
            @PathVariable String name,
            @RequestBody @Valid GoalUpdateRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.GOAL_UPDATED,
                goalService.updateGoal(name, request)
        );
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<String>> deleteGoal(
            @PathVariable String name
    ) {
        goalService.softDeleteGoal(name);
        return ResponseUtil.buildResponse(
                SuccessCode.GOAL_DELETED,
                null
        );
    }
    
}
