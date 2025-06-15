package com.swpteam.smokingcessation.service.impl.profile;

import com.swpteam.smokingcessation.domain.dto.setting.SettingRequest;
import com.swpteam.smokingcessation.domain.dto.setting.SettingResponse;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.domain.mapper.SettingMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.SettingRepository;
import com.swpteam.smokingcessation.service.interfaces.profile.ISettingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    @Transactional
    public SettingResponse createSetting(SettingRequest request) {
        Setting setting = settingMapper.toEntity(request);

        return settingMapper.toResponse(settingRepository.save(setting));
    }

    @Override
    @Transactional
    public SettingResponse updateSetting(String accountId, SettingRequest request) {
        Setting setting = settingRepository.findByAccountIdAndIsDeletedFalse(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        settingMapper.update(setting, request);

        return settingMapper.toResponse(settingRepository.save(setting));
    }

    @Override
    @Transactional
    public void deleteSetting(String accountId) {
        settingRepository.deleteById(accountId);
    }

    @Override
    public List<SettingResponse> getSettingList() {
        return settingRepository.findAll().stream().map(settingMapper::toResponse).toList();
    }

    @Override
    public SettingResponse getSetting(String accountId) {
        return settingMapper.toResponse(
                settingRepository.findById(accountId)
                        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND)));
    }
}
