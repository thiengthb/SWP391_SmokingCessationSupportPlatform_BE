package com.swpteam.smokingcessation.feature.version1.tracking.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.record.RecordHabitRequest;
import com.swpteam.smokingcessation.domain.dto.record.RecordHabitResponse;
import com.swpteam.smokingcessation.domain.entity.RecordHabit;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRecordHabitService {

    PageResponse<RecordHabitResponse> getMyRecordPage(PageableRequest request);

    RecordHabitResponse getRecordById(String id);

    PageResponse<RecordHabitResponse> getRecordPageByAccountId(String accountId, PageableRequest request);

    RecordHabitResponse createRecord(RecordHabitRequest request);

    RecordHabitResponse updateRecord(String id, RecordHabitRequest request);

    void softDeleteRecordById(String id);

    RecordHabit findRecordByIdOrThrowError(String id);

    List<RecordHabit> findAllByAccountIdAndDateBetweenAndIsDeletedFalse(String accountId, LocalDate start, LocalDate end);

    Optional<RecordHabit> getRecordByDate(String accountId, LocalDate date);

    Optional<RecordHabit> getLatestRecordBeforeDate(String accountId, LocalDate date);

    List<RecordHabit> getAllRecordNoSmoke(String accountId);

    boolean checkHabitRecordExistence(String accountId, LocalDate date);

    RecordHabit findRecordByDateOrNull(String accountId, LocalDate date);
}
