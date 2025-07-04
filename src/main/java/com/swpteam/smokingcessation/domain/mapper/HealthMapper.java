package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.health.HealthListItemResponse;
import com.swpteam.smokingcessation.domain.dto.health.HealthRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthResponse;
import com.swpteam.smokingcessation.domain.entity.Health;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HealthMapper {

    Health toEntity(HealthRequest request);

    @Mapping(source = "account.id", target = "accountId")
    HealthResponse toResponse(Health health);

    HealthListItemResponse toListResponse(Health entity);

    void update(@MappingTarget Health entity, HealthRequest request);
}