package com.swpteam.smokingcessation.apis.setting;

import com.swpteam.smokingcessation.apis.setting.dto.SettingRequest;
import com.swpteam.smokingcessation.apis.setting.dto.SettingResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SettingMapper {
    Setting toEntity(SettingRequest request);

    SettingResponse toResponse(Setting setting);
}
