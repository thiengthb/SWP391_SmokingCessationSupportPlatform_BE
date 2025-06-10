package com.swpteam.smokingcessation.apis.health;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.account.AccountRepository;
import com.swpteam.smokingcessation.apis.health.dto.HealthRequest;
import com.swpteam.smokingcessation.apis.health.dto.HealthUpdate;
import com.swpteam.smokingcessation.constants.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HealthService {
    HealthRepository healthRepository;
    AccountRepository accountRepository;
    HealthMapper healthMapper;

    public Health create(HealthRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        Health health = healthMapper.toHealth(request);
        health.setAccount(account);
        return healthRepository.save(health);
    }

    public List<Health> getAll() {
        return healthRepository.findAll();
    }

    public Health getById(UUID id) {
        return healthRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.HEALTH_RECORD_NOT_FOUND));
    }
    public boolean isHealthOwnedByUser(UUID healthId, String email) {
        return healthRepository.existsByIdAndAccount_Email(healthId, email);
    }
    public boolean isAccountOwnedByUser(String accountId, String email) {
        return healthRepository.existsAccountByIdAndEmail(accountId, email);
    }
    public List<Health> getAllByUserEmail(String email) {
        return healthRepository.findByAccount_Email(email);
    }
    public Health update(UUID id, HealthUpdate request) {
        Health health = healthRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.HEALTH_RECORD_NOT_FOUND));

        healthMapper.updateHealth(health, request);
        return healthRepository.save(health);
    }

    public void delete(UUID id) {
        if (!healthRepository.existsById(id)) {
            throw new AppException(ErrorCode.HEALTH_RECORD_NOT_FOUND);
        }
        healthRepository.deleteById(id);
    }
}