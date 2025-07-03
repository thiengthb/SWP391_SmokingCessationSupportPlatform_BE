package com.swpteam.smokingcessation.feature.version1.booking.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableRequest;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableResponse;
import com.swpteam.smokingcessation.domain.entity.TimeTable;

public interface ITimeTableService {

    PageResponse<TimeTableResponse> getTimeTablePage(PageableRequest request);

    PageResponse<TimeTableResponse> getMyTimeTablePage(PageableRequest request);

    PageResponse<TimeTableResponse> getTimeTablesByCoachId(String coachId, PageableRequest request);

    TimeTableResponse getTimeTableById(String id);

    TimeTableResponse createTimeTable(TimeTableRequest request);

    TimeTableResponse updateTimeTableById(String id, TimeTableRequest request);

    void softDeleteTimeTableById(String id);

    TimeTable findTimeTableByIdOrThrowError(String id);
}