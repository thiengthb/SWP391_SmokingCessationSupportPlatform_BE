package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableRequest;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableResponse;
import com.swpteam.smokingcessation.domain.entity.TimeTable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TimeTableMapper {

    @Mapping(source = "coach.id", target = "coachId")
    TimeTableResponse toResponse(TimeTable timeTable);

    TimeTable toEntity(TimeTableRequest timeTableRequest);

    void update(@MappingTarget TimeTable timeTable, TimeTableRequest request);
}