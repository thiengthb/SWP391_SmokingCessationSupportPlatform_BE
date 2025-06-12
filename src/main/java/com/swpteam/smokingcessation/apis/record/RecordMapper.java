package com.swpteam.smokingcessation.apis.record;

import com.swpteam.smokingcessation.apis.record.dto.RecordCreateRequest;
import com.swpteam.smokingcessation.apis.record.dto.RecordResponse;
import com.swpteam.smokingcessation.apis.record.dto.RecordUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RecordMapper {

    Record toEntity(RecordCreateRequest request);

    @Mapping(source = "account.id", target = "accountId")
    RecordResponse toResponse(Record record);

    void update(@MappingTarget Record entity, RecordUpdateRequest request);
}