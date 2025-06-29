package com.swpteam.smokingcessation.domain.dto.setting;

import com.swpteam.smokingcessation.domain.enums.Language;
import com.swpteam.smokingcessation.domain.enums.MotivationFrequency;
import com.swpteam.smokingcessation.domain.enums.Theme;
import com.swpteam.smokingcessation.domain.enums.TrackingMode;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record SettingRequest(

        @NotNull(message = "THEME_REQUIRED")
        Theme theme,

        @NotNull(message = "LANGUAGE_REQUIRED")
        Language language,

        @NotNull(message = "TRACKING_MODE_REQUIRED")
        TrackingMode trackingMode,

        @NotNull(message = "MOTIVATION_REQUIRED")
        MotivationFrequency motivationFrequency,

        @NotNull(message = "REPORT_DEADLINE_REQUIRED")
        LocalTime reportDeadline
) {}
