package com.swpteam.smokingcessation.apis.record;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.account.AccountRepository;
import com.swpteam.smokingcessation.apis.health.Health;
import com.swpteam.smokingcessation.apis.health.dto.HealthResponse;
import com.swpteam.smokingcessation.apis.record.dto.RecordCreateRequest;
import com.swpteam.smokingcessation.apis.record.dto.RecordResponse;
import com.swpteam.smokingcessation.apis.record.dto.RecordUpdateRequest;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constants.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordService {

    RecordRepository recordRepository;
    AccountRepository accountRepository;
    RecordMapper recordMapper;

    public Page<RecordResponse> getRecordPage(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Record.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Record> records = recordRepository.findAllByIsDeletedFalse(pageable);

        return records.map(recordMapper::toResponse);
    }

    public RecordResponse getRecordById(String id) {
        return recordMapper.toResponse(
                recordRepository.findByIdAndIsDeletedFalse(id)
                        .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND)));
    }

    public Page<RecordResponse> getRecordPageByAccountId(String accountId, PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Record.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Record> records = recordRepository.findByAccountIdAndIsDeletedFalse(accountId, pageable);

        return records.map(recordMapper::toResponse);
    }

    @Transactional
    public RecordResponse createRecord(RecordCreateRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Record record = recordMapper.toEntity(request);

        record.setAccount(account);

        return recordMapper.toResponse(recordRepository.save(record));
    }

    @Transactional
    public RecordResponse updateRecord(String id, RecordUpdateRequest request) {
        Record record = recordRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND));

        recordMapper.updateRecord(record, request);

        return recordMapper.toResponse(recordRepository.save(record));
    }

    @Transactional
    public void softDeleteRecordById(String id) {
        Record record = recordRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND));

        record.setDeleted(true);

        recordRepository.save(record);
    }
}