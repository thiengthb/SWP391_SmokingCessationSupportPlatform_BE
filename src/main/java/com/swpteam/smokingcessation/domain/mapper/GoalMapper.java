package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.goal.GoalCreateRequest;
import com.swpteam.smokingcessation.domain.dto.goal.GoalProgressResponse;
import com.swpteam.smokingcessation.domain.dto.goal.GoalResponse;
import com.swpteam.smokingcessation.domain.dto.goal.GoalUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Goal;
import com.swpteam.smokingcessation.domain.entity.GoalProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    Goal toEntity(GoalCreateRequest request);

    GoalResponse toResponse(Goal entity);

    @Mapping(target = "goalProgress", expression = "java(filterProgress(entity.getGoalProgresses(), accountId))")
    GoalResponse toResponse(Goal entity, String accountId);

    default GoalProgressResponse filterProgress(List<GoalProgress> progresses, String accountId) {
        if (progresses == null) return null;

        return progresses.stream()
                .filter(p -> p.getAccount().getId().equals(accountId))
                .findFirst()
                .map(p -> GoalProgressResponse.builder()
                        .id(p.getId())
                        .progress(p.getProgress().setScale(4))
                        .build()) // or use your mapper
                .orElse(null);
    }

    @Mapping(target = "goalProgress", ignore = true)
    GoalResponse toAdminResponse(Goal entity);

    void update(@MappingTarget Goal entity, GoalUpdateRequest request);
}
