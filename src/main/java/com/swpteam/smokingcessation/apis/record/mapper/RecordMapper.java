package com.swpteam.smokingcessation.apis.record.mapper;

import com.swpteam.smokingcessation.apis.record.DTO.request.RecordRequest;
import com.swpteam.smokingcessation.apis.record.DTO.request.RecordUpdate;
import com.swpteam.smokingcessation.apis.record.entity.Record;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RecordMapper {
    @Mapping(target = "account.id", source = "accountId")
    Record toRecord(RecordRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateRecord(@MappingTarget Record entity, RecordUpdate request);
}