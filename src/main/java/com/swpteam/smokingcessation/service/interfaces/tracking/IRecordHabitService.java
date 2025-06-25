package com.swpteam.smokingcessation.service.interfaces.tracking;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.record.RecordHabitCreateRequest;
import com.swpteam.smokingcessation.domain.dto.record.RecordHabitResponse;
import com.swpteam.smokingcessation.domain.dto.record.RecordHabitUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.RecordHabit;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface IRecordHabitService {

    PageResponse<RecordHabitResponse> getMyRecordPage(PageableRequest request);

    RecordHabitResponse getRecordById(String id);

    PageResponse<RecordHabitResponse> getRecordPageByAccountId(String accountId, PageableRequest request);

    RecordHabitResponse createRecord(RecordHabitCreateRequest request);

    RecordHabitResponse updateRecord(String id, RecordHabitUpdateRequest request);

    void softDeleteRecordById(String id);

    RecordHabit findRecordByIdOrThrowError(String id);

    List<RecordHabit> findAllByAccountIdAndDateBetweenAndIsDeletedFalse(String accountId, LocalDate start, LocalDate end);

}
