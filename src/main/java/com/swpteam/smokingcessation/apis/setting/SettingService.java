package com.swpteam.smokingcessation.apis.setting;

import com.swpteam.smokingcessation.apis.setting.dto.SettingRequest;
import com.swpteam.smokingcessation.apis.setting.dto.SettingResponse;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.constants.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SettingService {

    SettingRepository settingRepository;
    SettingMapper settingMapper;

    public SettingResponse createSetting(SettingRequest request) {
        Setting setting = settingMapper.toEntity(request);

        return settingMapper.toResponse(settingRepository.save(setting));
    }

    public SettingResponse updateSetting(String accountId, SettingRequest request) {
        Setting setting = settingRepository.findByAccountIdAndIsDeletedFalse(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        settingMapper.update(setting, request);

        return settingMapper.toResponse(settingRepository.save(setting));
    }

    public void deleteSetting(String accountId) {
        settingRepository.deleteById(accountId);
    }

    public List<SettingResponse> getSettingList() {
        return settingRepository.findAll().stream().map(settingMapper::toResponse).toList();
    }

    public SettingResponse getSetting(String accountId) {
        return settingMapper.toResponse(
                settingRepository.findById(accountId)
                        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED)));
    }
}
