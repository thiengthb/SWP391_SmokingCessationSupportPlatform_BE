package com.swpteam.smokingcessation.service.impl.profile;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.mapper.HealthMapper;
import com.swpteam.smokingcessation.domain.dto.health.HealthCreateRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthResponse;
import com.swpteam.smokingcessation.domain.dto.health.HealthUpdateRequest;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Health;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.HealthRepository;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.service.interfaces.profile.IHealthService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HealthServiceImpl implements IHealthService {

    HealthMapper healthMapper;
    HealthRepository healthRepository;

    IAccountService accountService;
    AuthUtilService authUtilService;

    @Override
    public Page<HealthResponse> getHealthPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Health.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Health> healths = healthRepository.findAllByIsDeletedFalse(pageable);

        return healths.map(healthMapper::toResponse);
    }

    @Override
    public Page<HealthResponse> getMyHealthPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Health.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Health> healths = healthRepository.findAllByAccountIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return healths.map(healthMapper::toResponse);
    }

    @Override
    public HealthResponse getHealthById(String id) {
        return healthMapper.toResponse(findHealthByIdOrThrowError(id));
    }

    @Override
    public Page<HealthResponse> getHealthPageByAccountId(String accountId, PageableRequest request) {
        ValidationUtil.checkFieldExist(Health.class, request.sortBy());

        accountService.findAccountByIdOrThrowError(accountId);

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Health> healths = healthRepository.findByAccountIdAndIsDeletedFalse(accountId, pageable);

        return healths.map(healthMapper::toResponse);
    }

    @Override
    @Transactional
    public HealthResponse createHealth(HealthCreateRequest request) {
        Account account = authUtilService.getCurrentAccountOrThrowError();

        Health health = healthMapper.toEntity(request);
        health.setAccount(account);

        return healthMapper.toResponse(healthRepository.save(health));
    }

    @Override
    @Transactional
    public HealthResponse updateHealth(String id, HealthUpdateRequest request) {
        Health health = findHealthByIdOrThrowError(id);

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
}