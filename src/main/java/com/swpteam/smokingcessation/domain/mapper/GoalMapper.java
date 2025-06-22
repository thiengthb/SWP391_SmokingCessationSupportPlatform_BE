package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.goal.*;
import com.swpteam.smokingcessation.domain.entity.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    Goal toEntity(GoalCreateRequest request);

    GoalResponse toResponse(Goal entity);

    void update(@MappingTarget Goal entity, GoalUpdateRequest request);
}
