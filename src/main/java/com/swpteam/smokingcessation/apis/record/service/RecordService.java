package com.swpteam.smokingcessation.apis.record.service;

import com.swpteam.smokingcessation.apis.account.entity.Account;
import com.swpteam.smokingcessation.apis.account.repository.AccountRepository;
import com.swpteam.smokingcessation.apis.record.DTO.request.RecordRequest;
import com.swpteam.smokingcessation.apis.record.DTO.request.RecordUpdate;
import com.swpteam.smokingcessation.apis.record.entity.Record;
import com.swpteam.smokingcessation.apis.record.mapper.RecordMapper;
import com.swpteam.smokingcessation.apis.record.repository.RecordRepository;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordService {
    RecordRepository recordRepository;
    AccountRepository accountRepository;
    RecordMapper recordMapper;

    public Record create(RecordRequest request) {
        String accountId = request.getAccountId();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_ACCOUNT_ID));

        Optional<Record> existingRecord = recordRepository.findByDateAndAccount_Id(
                request.getDate(), accountId
        );

        if (existingRecord.isPresent()) {
            throw new AppException(ErrorCode.RECORD_ALREADY_EXISTS);
        }

        Record record = recordMapper.toRecord(request);
        record.setAccount(account);
        return recordRepository.save(record);
    }

    public List<Record> getAll() {
        return recordRepository.findAll();
    }

    public Record getById(UUID id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND));
    }

    public Record update(UUID id, RecordUpdate request) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND));

        String accountId = record.getAccount().getId();

        if (!record.getDate().equals(request.getDate())) {
            Optional<Record> existingRecord = recordRepository.findByDateAndAccount_Id(request.getDate(), accountId);

            if (existingRecord.isPresent() && !existingRecord.get().getId().equals(record.getId())) {
                throw new AppException(ErrorCode.RECORD_ALREADY_EXISTS);
            }
        }

        recordMapper.updateRecord(record, request);
        return recordRepository.save(record);
    }

    public void delete(UUID id) {
        if (!recordRepository.existsById(id)) {
            throw new AppException(ErrorCode.RECORD_NOT_FOUND);
        }
        recordRepository.deleteById(id);
    }
}
