package com.swpteam.smokingcessation.domain.dto.setting;

import com.swpteam.smokingcessation.domain.enums.Language;
import com.swpteam.smokingcessation.domain.enums.MotivationFrequency;
import com.swpteam.smokingcessation.domain.enums.Theme;
import com.swpteam.smokingcessation.domain.enums.TrackingMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record SettingRequest(

        @NotNull(message = "THEME_REQUIRED")
        Theme theme,

        @NotNull(message = "Language is required")
        Language language,

        @NotNull(message = "Tracking mode is required")
        TrackingMode trackingMode,

        @NotNull(message = "Motivation frequency is required")
        MotivationFrequency motivationFrequency,

        @NotNull(message = "Report deadline is required")
        LocalTime reportDeadline
) {}
