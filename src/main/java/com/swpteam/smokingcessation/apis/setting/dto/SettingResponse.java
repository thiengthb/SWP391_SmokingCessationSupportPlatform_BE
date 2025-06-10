package com.swpteam.smokingcessation.apis.setting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.apis.setting.enums.Language;
import com.swpteam.smokingcessation.apis.setting.enums.MotivationFrequency;
import com.swpteam.smokingcessation.apis.setting.enums.Theme;
import com.swpteam.smokingcessation.apis.setting.enums.TrackingMode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettingResponse {
    private String accountId;
    private Theme theme;
    private Language language;
    private TrackingMode trackingMode;
    private MotivationFrequency motivationFrequency;
    private LocalTime reportDeadline;
}
