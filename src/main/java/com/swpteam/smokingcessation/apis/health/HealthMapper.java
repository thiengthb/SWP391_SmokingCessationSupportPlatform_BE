package com.swpteam.smokingcessation.apis.health;

import com.swpteam.smokingcessation.apis.health.dto.HealthRequest;
import com.swpteam.smokingcessation.apis.health.dto.HealthResponse;
import com.swpteam.smokingcessation.apis.health.dto.HealthUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HealthMapper {
    @Mapping(target = "account.id", source = "accountId")
    Health toHealth(HealthRequest request);

    @Mapping(target = "accountId", source = "account.id")
    HealthResponse toResponse(Health health);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    void updateHealth(@MappingTarget Health entity, HealthUpdate request);
}