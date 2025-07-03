package com.swpteam.smokingcessation.service.interfaces.booking;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableRequest;
import com.swpteam.smokingcessation.domain.dto.timetable.TimeTableResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.TimeTable;

import java.time.LocalDateTime;

public interface ITimeTableService {

    PageResponse<TimeTableResponse> getTimeTablePage(PageableRequest request);

    PageResponse<TimeTableResponse> getMyTimeTablePage(PageableRequest request);

    PageResponse<TimeTableResponse> getTimeTablesByCoachId(String coachId, PageableRequest request);

    TimeTableResponse getTimeTableById(String id);

    TimeTableResponse createTimeTable(TimeTableRequest request);

    TimeTableResponse updateTimeTableById(String id, TimeTableRequest request);

    void softDeleteTimeTableById(String id);

    TimeTable findTimeTableByIdOrThrowError(String id);

    void createTimeTableAuto(LocalDateTime start, LocalDateTime end, Account coach);

    boolean isBookingTimeInAnyTimeTable(LocalDateTime bookingStart, LocalDateTime bookingEnd, String coachId);
}