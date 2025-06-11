package com.swpteam.smokingcessation.apis.health;

import com.swpteam.smokingcessation.apis.health.dto.HealthCreateRequest;
import com.swpteam.smokingcessation.apis.health.dto.HealthResponse;
import com.swpteam.smokingcessation.apis.health.dto.HealthUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HealthMapper {

    Health toEntity(HealthCreateRequest request);

    @Mapping(source = "account.id", target = "accountId")
    HealthResponse toResponse(Health health);

    void updateHealth(@MappingTarget Health entity, HealthUpdateRequest request);
}