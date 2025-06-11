package com.swpteam.smokingcessation.apis.record;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.account.AccountRepository;
import com.swpteam.smokingcessation.apis.record.dto.RecordRequest;
import com.swpteam.smokingcessation.apis.record.dto.RecordResponse;
import com.swpteam.smokingcessation.apis.record.dto.RecordUpdate;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RecordService {
    RecordRepository recordRepository;
    AccountRepository accountRepository;
    RecordMapper recordMapper;

    public RecordResponse create(RecordRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        Record record = recordMapper.toRecord(request);
        record.setAccount(account);
        return recordMapper.toResponse(recordRepository.save(record));
    }

    public Page<RecordResponse> getAllByUserEmail(PageableRequest request, String email) {
        if (!ValidationUtil.isFieldExist(Record.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Record> records = recordRepository.findByAccount_Email(email, pageable);
        return records.map(recordMapper::toResponse);
    }

    public RecordResponse getById(UUID id) {
        return recordMapper.toResponse(
                recordRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND)));
    }

    public boolean isRecordOwnedByUser(UUID recordId, String email) {
        return recordRepository.existsByIdAndAccount_Email(recordId, email);
    }

    public boolean isAccountOwnedByUser(String accountId, String email) {
        return recordRepository.existsAccountByIdAndEmail(accountId, email);
    }

    public RecordResponse update(UUID id, RecordUpdate request) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND));

        recordMapper.updateRecord(record, request);
        return recordMapper.toResponse(recordRepository.save(record));
    }

    public void delete(UUID id) {
        if (!recordRepository.existsById(id)) {
            throw new AppException(ErrorCode.RECORD_NOT_FOUND);
        }
        recordRepository.deleteById(id);
    }
}