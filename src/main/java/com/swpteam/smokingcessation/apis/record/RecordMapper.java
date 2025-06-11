package com.swpteam.smokingcessation.apis.record;

import com.swpteam.smokingcessation.apis.record.dto.RecordRequest;
import com.swpteam.smokingcessation.apis.record.dto.RecordResponse;
import com.swpteam.smokingcessation.apis.record.dto.RecordUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RecordMapper {

    @Mapping(target = "account.id", source = "accountId")
    Record toRecord(RecordRequest request);

    @Mapping(target = "accountId", source = "account.id")
    RecordResponse toResponse(Record record);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateRecord(@MappingTarget Record entity, RecordUpdate request);
}