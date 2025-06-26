package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.goal.GoalCreateRequest;
import com.swpteam.smokingcessation.domain.dto.goal.GoalDetailsResponse;
import com.swpteam.smokingcessation.domain.dto.goal.GoalListItemResponse;
import com.swpteam.smokingcessation.domain.dto.goal.GoalUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    Goal toEntity(GoalCreateRequest request);

    GoalDetailsResponse toResponse(Goal entity);

    GoalListItemResponse toResponse(GoalDetailsResponse entity, String accountId);

    @Mapping(target = "goalProgress", ignore = true)
    GoalListItemResponse toAdminResponse(Goal entity);

    default GoalDetailsResponse mapRowToGoalDetails(Object[] row) {
        Timestamp createdAtTs = (Timestamp) row[6];
        Timestamp updatedAtTs = (Timestamp) row[7];

        return GoalDetailsResponse.builder()
                .id((String) row[0])
                .name((String) row[1])
                .iconUrl((String) row[2])
                .description((String) row[3])
                .criteriaType((String) row[4])
                .criteriaValue((Integer) row[5])
                .createdAt(createdAtTs != null ? createdAtTs.toLocalDateTime() : null)
                .updatedAt(updatedAtTs != null ? updatedAtTs.toLocalDateTime() : null)
                .goalProgress((BigDecimal) row[8])
                .build();
    }

    void update(@MappingTarget Goal entity, GoalUpdateRequest request);
}
