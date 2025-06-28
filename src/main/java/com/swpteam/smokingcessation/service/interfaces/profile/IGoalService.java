package com.swpteam.smokingcessation.service.interfaces.profile;

import com.swpteam.smokingcessation.domain.dto.goal.GoalCreateRequest;
import com.swpteam.smokingcessation.domain.dto.goal.GoalDetailsResponse;
import com.swpteam.smokingcessation.domain.dto.goal.GoalListItemResponse;
import com.swpteam.smokingcessation.domain.dto.goal.GoalUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Goal;

import java.util.List;

public interface IGoalService {

    List<GoalListItemResponse> getPublicGoals();

    List<GoalListItemResponse> getMyGoals();

    GoalDetailsResponse getGoalByName(String name);

    GoalDetailsResponse createGoal(GoalCreateRequest request);

    GoalDetailsResponse updateGoal(String name, GoalUpdateRequest request);

    GoalDetailsResponse getMyGoalDetailsById(String id);

    void softDeleteGoal(String name);

    Goal findGoalByName(String name);

    Goal getGoalByIdOrThrow(String id);
}
