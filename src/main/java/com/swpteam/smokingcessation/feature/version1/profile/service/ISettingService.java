package com.swpteam.smokingcessation.feature.version1.profile.service;

import com.swpteam.smokingcessation.domain.dto.setting.SettingResponse;
import com.swpteam.smokingcessation.domain.dto.setting.SettingRequest;
import com.swpteam.smokingcessation.domain.entity.Setting;

import java.util.List;

public interface ISettingService {

    SettingResponse getMySetting();

    SettingResponse getSettingByAccountId(String accountId);

    SettingResponse updateSetting(String accountId, SettingRequest request);

    SettingResponse resetMySetting();

    Setting findSettingByIdOrThrowError(String accountId);

    void softDeleteSettingById(String accountId);

    public List<Setting> getAllSetting();
}
