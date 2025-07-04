package com.swpteam.smokingcessation.feature.version1.profile.service.impl;

import com.swpteam.smokingcessation.domain.dto.setting.SettingRequest;
import com.swpteam.smokingcessation.domain.dto.setting.SettingResponse;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.domain.enums.TrackingMode;
import com.swpteam.smokingcessation.domain.mapper.SettingMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.jpa.AccountRepository;
import com.swpteam.smokingcessation.repository.jpa.SettingRepository;
import com.swpteam.smokingcessation.feature.version1.profile.service.ISettingService;
import com.swpteam.smokingcessation.feature.version1.tracking.service.ICounterService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SettingServiceImpl implements ISettingService {

    SettingRepository settingRepository;
    SettingMapper settingMapper;
    AccountRepository accountRepository;
    AuthUtilService authUtilService;
    ICounterService counterService;

    @Override
    @Cacheable(value = "SETTING_CACHE", key = "@authUtilService.getCurrentAccountOrThrowError().id")
    public SettingResponse getMySetting() {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();
        return settingMapper.toResponse(findSettingByIdOrThrowError(currentAccount.getId()));
    }

    @Override
    @Cacheable(value = "SETTING_CACHE", key = "#accountId")
    public SettingResponse getSettingByAccountId(String accountId) {
        return settingMapper.toResponse(findSettingByIdOrThrowError(accountId));
    }
    
    @Override
    @Transactional
    @CachePut(value = "SETTING_CACHE", key = "#accountId")
    public SettingResponse updateSetting(String accountId, SettingRequest request) {
        Setting setting = findSettingByIdOrThrowError(accountId);

        if(setting.isChangeFlag() && setting.getTrackingMode() != request.trackingMode()){
            throw new AppException(ErrorCode.MODE_CHANGE_UNAVAILABLE);
        }

        if (setting.getTrackingMode() == TrackingMode.AUTO_COUNT
                && request.trackingMode() == TrackingMode.DAILY_RECORD
        ) {
            counterService.startCounter();
            setting.setChangeFlag(true);
        }

        settingMapper.update(setting, request);

        return settingMapper.toResponse(settingRepository.save(setting));
    }

    @Override
    @Transactional
    @CachePut(value = "SETTING_CACHE", key = "#accountId")
    public SettingResponse resetMySetting() {
        Account account = authUtilService.getCurrentAccountOrThrowError();

        Setting resetSetting = Setting.getDefaultSetting(account);
        account.setSetting(resetSetting);
        accountRepository.save(account);

        return settingMapper.toResponse(settingRepository.save(resetSetting));
    }

    @Override
    public Setting findSettingByIdOrThrowError(String accountId) {
        Setting setting = settingRepository.findByIdAndIsDeletedFalse(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (setting.getAccount().isDeleted()) {
            setting.setDeleted(true);
            settingRepository.save(setting);
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return setting;
    }

    @Override
    @CacheEvict(value = "SETTING_CACHE", key = "#accountId")
    public void softDeleteSettingById(String accountId) {

        Setting setting = findSettingByIdOrThrowError(accountId);

        setting.setDeleted(true);

        settingRepository.save(setting);
    }

    @Override
    public List<Setting> getAllSetting(){
        List<Setting> settings= settingRepository.findAllWhereAccountNotAdminOrCoach();
        if(settings.isEmpty()){
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        return settings;
    }
}
