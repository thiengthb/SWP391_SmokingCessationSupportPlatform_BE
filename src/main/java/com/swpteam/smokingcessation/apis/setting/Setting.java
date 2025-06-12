package com.swpteam.smokingcessation.apis.setting;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.setting.enums.Language;
import com.swpteam.smokingcessation.apis.setting.enums.MotivationFrequency;
import com.swpteam.smokingcessation.apis.setting.enums.Theme;
import com.swpteam.smokingcessation.apis.setting.enums.TrackingMode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Setting {

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "account_id")
    Account account;

    @Enumerated(EnumType.STRING)
    Theme theme;

    @Enumerated(EnumType.STRING)
    Language language;

    @Enumerated(EnumType.STRING)
    TrackingMode trackingMode;

    @Enumerated(EnumType.STRING)
    MotivationFrequency motivationFrequency;

    LocalTime reportDeadline;

    public static Setting getDefaultSetting(Account account) {
        return Setting.builder()
                .account(account)
                .theme(Theme.LIGHT)
                .language(Language.EN)
                .motivationFrequency(MotivationFrequency.NEVER)
                .trackingMode(TrackingMode.AUTO_COUNTER)
                .reportDeadline(LocalTime.of(22, 0))
                .build();
    }
}
