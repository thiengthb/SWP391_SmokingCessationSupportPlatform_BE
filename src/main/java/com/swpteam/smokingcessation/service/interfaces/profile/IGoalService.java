package com.swpteam.smokingcessation.service.interfaces.profile;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.goal.GoalCreateRequest;
import com.swpteam.smokingcessation.domain.dto.goal.GoalResponse;
import com.swpteam.smokingcessation.domain.dto.goal.GoalUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Goal;
import org.springframework.data.domain.Page;

public interface IGoalService {

    Page<GoalResponse> getPublicGoalPage(PageableRequest request);

    Page<GoalResponse> getMyGoalPage(PageableRequest request);

    GoalResponse getGoalByName(String name);

    GoalResponse createGoal(GoalCreateRequest request);

    GoalResponse updateGoal(String name, GoalUpdateRequest request);

    void softDeleteGoal(String name);

    Goal findGoalByName(String name);
}
