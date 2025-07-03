package com.swpteam.smokingcessation.service.impl.tracking;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.record.RecordHabitRequest;
import com.swpteam.smokingcessation.domain.dto.record.RecordHabitResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.RecordHabit;
import com.swpteam.smokingcessation.domain.entity.Streak;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.domain.enums.TrackingMode;
import com.swpteam.smokingcessation.domain.mapper.RecordHabitMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.RecordHabitRepository;
import com.swpteam.smokingcessation.repository.StreakRepository;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.service.interfaces.profile.IScoreService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IRecordHabitService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IStreakService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordHabitServiceImpl implements IRecordHabitService {

    RecordHabitMapper recordHabitMapper;
    RecordHabitRepository recordHabitRepository;
    StreakRepository streakRepository;
    IStreakService streakService;
    IAccountService accountService;
    AuthUtilService authUtilService;
    IScoreService scoreService;

    @Override
    public PageResponse<RecordHabitResponse> getMyRecordPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(RecordHabitMapper.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<RecordHabit> records = recordHabitRepository.findByAccountIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(records.map(recordHabitMapper::toResponse));
    }

    @Override
    public RecordHabitResponse getRecordById(String id) {
        return recordHabitMapper.toResponse(findRecordByIdOrThrowError(id));
    }

    @Override
    public PageResponse<RecordHabitResponse> getRecordPageByAccountId(String accountId, PageableRequest request) {
        ValidationUtil.checkFieldExist(RecordHabitMapper.class, request.sortBy());

        accountService.findAccountByIdOrThrowError(accountId);

        Pageable pageable = PageableRequest.getPageable(request);
        Page<RecordHabit> records = recordHabitRepository.findByAccountIdAndIsDeletedFalse(accountId, pageable);

        return new PageResponse<>(records.map(recordHabitMapper::toResponse));
    }

    @Override
    @Transactional
    public RecordHabitResponse createRecord(RecordHabitRequest request) {
        Account curentAccount = authUtilService.getCurrentAccountOrThrowError();
        boolean existed = recordHabitRepository
                .existsByAccountIdAndDateAndIsDeletedFalse(curentAccount.getId(), request.date());
        if (existed) {
            throw new AppException(ErrorCode.RECORD_ALREADY_EXISTS);
        }

        RecordHabit recordHabit = recordHabitMapper.toEntity(request);
        recordHabit.setAccount(curentAccount);

        Streak streak = streakRepository.findByAccountIdAndIsDeletedFalse(curentAccount.getId())
                .orElseGet(() -> streakService.createStreak(curentAccount.getId(), 0));

        streakService.updateStreak(curentAccount.getId(), streak.getNumber() + 1);
        if(curentAccount.getSetting().getTrackingMode()== TrackingMode.DAILY_RECORD){
            scoreService.updateScore(curentAccount.getId(), ScoreRule.REPORT_DAY_HAS);
        }else{
            scoreService.updateScore(curentAccount.getId(),ScoreRule.NO_SMOKING_DAY_AUTO_COUNTER);
        }
        return recordHabitMapper.toResponse(recordHabitRepository.save(recordHabit));
    }

    @Override
    @Transactional
    public RecordHabitResponse updateRecord(String id, RecordHabitRequest request) {
        RecordHabit recordHabit = findRecordByIdOrThrowError(id);

        recordHabitMapper.update(recordHabit, request);

        return recordHabitMapper.toResponse(recordHabitRepository.save(recordHabit));
    }

    @Override
    @Transactional
    public void softDeleteRecordById(String id) {
        RecordHabit recordHabit = findRecordByIdOrThrowError(id);

        recordHabit.setDeleted(true);

        recordHabitRepository.save(recordHabit);
    }

    @Override
    @Transactional
    public RecordHabit findRecordByIdOrThrowError(String id) {
        RecordHabit recordHabit = recordHabitRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HEALTH_RECORD_NOT_FOUND));

        if (recordHabit.getAccount().isDeleted()) {
            recordHabit.setDeleted(true);
            recordHabitRepository.save(recordHabit);
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return recordHabit;
    }

    @Override
    public List<RecordHabit> findAllByAccountIdAndDateBetweenAndIsDeletedFalse(String accountId, LocalDate start, LocalDate end) {
        return recordHabitRepository.findAllByAccountIdAndDateBetweenAndIsDeletedFalse(accountId, start, end)
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND));
    }

    @Override
    public Optional<RecordHabit> getRecordByDate(String accountId, LocalDate date) {
        return recordHabitRepository.findByAccountIdAndDate(accountId, date);
    }

    @Override
    public Optional<RecordHabit> getLatestRecordBeforeDate(String accountId, LocalDate date) {
        return recordHabitRepository.findTopByAccountIdAndDateLessThanOrderByDateDesc(accountId, date);
    }

    @Override
    public List<RecordHabit> getAllRecordNoSmoke(String accountId) {
        List<RecordHabit> recordHabits = recordHabitRepository.findAllByAccountIdWithNoCigarettesSmoked(accountId);
        log.info("list record with record 0 smoke {}", recordHabits.size());
        return recordHabits;
    }

    @Override
    public boolean checkHabitRecordExistence(String accountId, LocalDate date){
        return recordHabitRepository.existsByAccountIdAndDateAndIsDeletedFalse(accountId, date);
    }

    @Override
    public RecordHabit findRecordByDateOrNull(String accountId, LocalDate date){
        return recordHabitRepository.findByAccountIdAndDate(accountId, date).orElse(null);
    }
}