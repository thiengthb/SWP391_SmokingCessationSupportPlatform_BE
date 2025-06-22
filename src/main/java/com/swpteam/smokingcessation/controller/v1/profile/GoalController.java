package com.swpteam.smokingcessation.controller.v1.profile;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.goal.*;
import com.swpteam.smokingcessation.service.interfaces.profile.IGoalService;
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
    public ResponseEntity<ApiResponse<Page<GoalResponse>>> getGoalPage(@Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<GoalResponse>>builder()
                        .code(SuccessCode.GOAL_GET_ALL.getCode())
                        .message(SuccessCode.GOAL_GET_ALL.getMessage())
                        .result(goalService.getPublicGoalPage(request))
                        .build()
        );
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<GoalResponse>> getGoalByName(@PathVariable String name) {
        return ResponseEntity.ok(
                ApiResponse.<GoalResponse>builder()
                        .code(SuccessCode.GOAL_GET_BY_NAME.getCode())
                        .message(SuccessCode.GOAL_GET_BY_NAME.getMessage())
                        .result(goalService.getGoalByName(name))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GoalResponse>> createGoal(
            @RequestBody @Valid GoalCreateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<GoalResponse>builder()
                        .code(SuccessCode.GOAL_CREATED.getCode())
                        .message(SuccessCode.GOAL_CREATED.getMessage())
                        .result(goalService.createGoal(request))
                        .build()
        );
    }

    @PutMapping("/{name}")
    public ResponseEntity<ApiResponse<GoalResponse>> updateGoal(
            @PathVariable String name,
            @RequestBody @Valid GoalUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<GoalResponse>builder()
                        .code(SuccessCode.GOAL_UPDATED.getCode())
                        .message(SuccessCode.GOAL_UPDATED.getMessage())
                        .result(goalService.updateGoal(name, request))
                        .build()
        );
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<String>> deleteGoal(@PathVariable String name) {
        goalService.softDeleteGoal(name);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .code(SuccessCode.GOAL_DELETED.getCode())
                        .message(SuccessCode.GOAL_DELETED.getMessage())
                        .build()
        );
    }
}
