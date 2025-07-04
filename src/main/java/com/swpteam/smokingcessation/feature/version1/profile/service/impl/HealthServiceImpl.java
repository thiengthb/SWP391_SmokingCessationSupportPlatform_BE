package com.swpteam.smokingcessation.feature.version1.profile.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.health.HealthListItemResponse;
import com.swpteam.smokingcessation.domain.dto.health.HealthRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Health;
import com.swpteam.smokingcessation.domain.mapper.HealthMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.version1.identity.service.IAccountService;
import com.swpteam.smokingcessation.feature.version1.profile.service.IHealthService;
import com.swpteam.smokingcessation.repository.jpa.HealthRepository;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HealthServiceImpl implements IHealthService {

    HealthMapper healthMapper;
    HealthRepository healthRepository;
    ObjectMapper objectMapper;
    IAccountService accountService;
    AuthUtilService authUtilService;

    @Override
    public PageResponse<HealthListItemResponse> getHealthPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Health.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Health> healths = healthRepository.findAllByIsDeletedFalse(pageable);

        return new PageResponse<>(healths.map(healthMapper::toListResponse));
    }

    @Override
    public boolean hasCompleteFTNDAssessment() {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();
        return healthRepository.existsByAccountId(currentAccount.getId());
    }

    @Override
    public PageResponse<HealthListItemResponse> getMyHealthPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Health.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Health> healths = healthRepository.findAllByAccountIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(healths.map(healthMapper::toListResponse));
    }

    @Override
    public HealthResponse getMyLastestHealth() {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Health lastestHealth = findLatestHealthByAccountIdOrThrowError(currentAccount.getId());

        return healthMapper.toResponse(lastestHealth);
    }

    @Override
    public HealthResponse getHealthById(String id) {
        return healthMapper.toResponse(findHealthByIdOrThrowError(id));
    }

    @Override
    public PageResponse<HealthListItemResponse> getHealthPageByAccountId(String accountId, PageableRequest request) {
        ValidationUtil.checkFieldExist(Health.class, request.sortBy());

        accountService.findAccountByIdOrThrowError(accountId);

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Health> healths = healthRepository.findByAccountIdAndIsDeletedFalse(accountId, pageable);

        return new PageResponse<>(healths.map(healthMapper::toListResponse));
    }

    @Override
    @Transactional
    public HealthResponse createHealth(HealthRequest request) {
        Account account = authUtilService.getCurrentAccountOrThrowError();

        List<Health> healths = findHealthRecordsToday();
        if (!healths.isEmpty()) {
            throw new AppException(ErrorCode.HEALTH_RECORD_ALREADY_TAKEN_TODAY);
        }

        try {
            objectMapper.readTree(request.ftndAnswers()); // xác thực JSON hợp lệ
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_ERROR);
        }

        Health health = healthMapper.toEntity(request);
        health.setAccount(account);

        return healthMapper.toResponse(healthRepository.save(health));
    }

    private List<Health> findHealthRecordsToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return healthRepository.findByCreatedAtBetween(startOfDay, endOfDay);
    }

    @Override
    @Transactional
    public HealthResponse updateHealth(String id, HealthRequest request) {
        Health health = findHealthByIdOrThrowError(id);

        long days = ChronoUnit.DAYS.between(health.getCreatedAt(), LocalDateTime.now());
        if (days >= 1)
            throw new AppException(ErrorCode.HEALTH_RECORD_UPDATE_FAILED);

        try {
            objectMapper.readTree(request.ftndAnswers()); // xác thực JSON hợp lệ
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_ERROR);
        }

        healthMapper.update(health, request);

        return healthMapper.toResponse(healthRepository.save(health));
    }

    @Override
    @Transactional
    public void softDeleteHealthById(String id) {
        Health health = findHealthByIdOrThrowError(id);

        health.setDeleted(true);
        
        healthRepository.save(health);
    }

    public Health findHealthByIdOrThrowError(String id) {
        Health health = healthRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HEALTH_RECORD_NOT_FOUND));

        if (health.getAccount().isDeleted()) {
            throw new AppException(ErrorCode.HEALTH_RECORD_NOT_FOUND);
        }

        return health;
    }

    @Override
    @Transactional
    public Health findLatestHealthByAccountIdOrThrowError(String accountId){
        Health health = healthRepository.findFirstByAccountIdAndIsDeletedFalseOrderByCreatedAtDesc(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.HEALTH_RECORD_NOT_FOUND));

        if(health.getAccount().isDeleted()){
            health.setDeleted(true);
            healthRepository.save(health);
            throw new AppException(ErrorCode.HEALTH_RECORD_NOT_FOUND);
        }

        return health;
    }

    @Override
    public Health findLatestHealthByAccountIdOrNull(String accountId){
        return healthRepository.findFirstByAccountIdAndIsDeletedFalseOrderByCreatedAtDesc(accountId)
                .orElse(null);
    }
}