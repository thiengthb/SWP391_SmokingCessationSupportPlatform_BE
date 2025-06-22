package com.swpteam.smokingcessation.service.interfaces.booking;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableRequest;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableResponse;
import com.swpteam.smokingcessation.domain.entity.TimeTable;
import org.springframework.data.domain.Page;

public interface ITimeTableService {
    Page<TimeTableResponse> getTimeTablePage(PageableRequest request);

    Page<TimeTableResponse> getMyTimeTablePage(PageableRequest request);

    TimeTableResponse getTimeTableById(String id);

    Page<TimeTableResponse> getTimeTablesByCoachId(String coachId, PageableRequest request);

    TimeTableResponse createTimeTable(TimeTableRequest request);

    TimeTableResponse updateTimeTableById(String id, TimeTableRequest request);

    void softDeleteTimeTableById(String id);

    TimeTable findTimeTableByIdOrThrowError(String id);
}