package com.swpteam.smokingcessation.apis.health.mapper;

import com.swpteam.smokingcessation.apis.health.DTO.request.HealthRequest;
import com.swpteam.smokingcessation.apis.health.DTO.request.HealthUpdate;
import com.swpteam.smokingcessation.apis.health.entity.Health;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HealthMapper {
    @Mapping(target = "account.id", source = "accountId")
    Health toHealth(HealthRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    void updateHealth(@MappingTarget Health entity, HealthUpdate request);
}