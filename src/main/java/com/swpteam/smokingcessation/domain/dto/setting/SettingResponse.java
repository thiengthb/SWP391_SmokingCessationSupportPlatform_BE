package com.swpteam.smokingcessation.domain.dto.setting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.domain.enums.Language;
import com.swpteam.smokingcessation.domain.enums.MotivationFrequency;
import com.swpteam.smokingcessation.domain.enums.Theme;
import com.swpteam.smokingcessation.domain.enums.TrackingMode;
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
    Theme theme;
    Language language;
    TrackingMode trackingMode;
    MotivationFrequency motivationFrequency;
    LocalTime reportDeadline;
}
