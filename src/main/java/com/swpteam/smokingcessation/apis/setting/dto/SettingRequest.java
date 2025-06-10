package com.swpteam.smokingcessation.apis.setting.dto;

import com.swpteam.smokingcessation.apis.setting.enums.Language;
import com.swpteam.smokingcessation.apis.setting.enums.MotivationFrequency;
import com.swpteam.smokingcessation.apis.setting.enums.Theme;
import com.swpteam.smokingcessation.apis.setting.enums.TrackingMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettingRequest {
    @NotBlank(message = "ACCOUNT_NOT_BLANK")
    private String accountId;

    @NotNull(message = "THEME_REQUIRED")
    private Theme theme;

    @NotNull(message = "Language is required")
    private Language language;

    @NotNull(message = "Tracking mode is required")
    private TrackingMode trackingMode;

    @NotNull(message = "Motivation frequency is required")
    private MotivationFrequency motivationFrequency;

    @NotNull(message = "Report deadline is required")
    private LocalTime reportDeadline;
}
