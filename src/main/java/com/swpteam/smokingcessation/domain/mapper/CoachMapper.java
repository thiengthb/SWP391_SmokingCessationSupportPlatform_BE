package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.coach.CoachCreateRequest;
import com.swpteam.smokingcessation.domain.dto.coach.CoachResponse;
import com.swpteam.smokingcessation.domain.dto.coach.CoachUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Coach;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CoachMapper {
    @Mapping(source = "account.id", target = "accountId")
    CoachResponse toResponse(Coach coach);

    Coach toEntity(CoachCreateRequest coachCreateRequest);

    void update(@MappingTarget Coach coach, CoachUpdateRequest request);
}