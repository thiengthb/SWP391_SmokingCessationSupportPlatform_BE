package com.swpteam.smokingcessation.apis.health;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.account.AccountRepository;
import com.swpteam.smokingcessation.apis.health.dto.HealthCreateRequest;
import com.swpteam.smokingcessation.apis.health.dto.HealthResponse;
import com.swpteam.smokingcessation.apis.health.dto.HealthUpdateRequest;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class HealthService {
    HealthRepository healthRepository;
    AccountRepository accountRepository;
    HealthMapper healthMapper;

    public Page<HealthResponse> getHealthPage(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Health.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Health> healths = healthRepository.findAllByIsDeletedFalse(pageable);

        return healths.map(healthMapper::toResponse);
    }

    public HealthResponse getHealthById(String id) {
        return healthMapper.toResponse(
                healthRepository.findByIdAndIsDeletedFalse(id)
                        .orElseThrow(() -> new AppException(ErrorCode.HEALTH_RECORD_NOT_FOUND)));
    }

    public Page<HealthResponse> getHealthPageByAccountId(String accountId, PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Health.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Health> healths = healthRepository.findByAccountIdAndIsDeletedFalse(accountId, pageable);

        return healths.map(healthMapper::toResponse);
    }

    @Transactional
    public HealthResponse createHealth(HealthCreateRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        Health health = healthMapper.toEntity(request);

        health.setAccount(account);

        return healthMapper.toResponse(healthRepository.save(health));
    }

    @Transactional
    public HealthResponse updateHealth(String id, HealthUpdateRequest request) {
        Health health = healthRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HEALTH_RECORD_NOT_FOUND));

        healthMapper.updateHealth(health, request);

        return healthMapper.toResponse(healthRepository.save(health));
    }

    @Transactional
    public void softDeleteHealthById(String id) {
        Health health = healthRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HEALTH_RECORD_NOT_FOUND));

        health.setDeleted(true);

        healthRepository.save(health);
    }
}