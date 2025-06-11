package com.swpteam.smokingcessation.apis.setting;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.setting.enums.Language;
import com.swpteam.smokingcessation.apis.setting.enums.Theme;
import com.swpteam.smokingcessation.apis.setting.enums.TrackingMode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Setting {
    @Id
    private String accountId;

    private Theme theme;
    private Language language;
    private TrackingMode trackingMode;
    private Integer motivationPerDay;
    private LocalTime reportDeadline;
    private LocalDateTime updatedAt;

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    public static Setting getDefaultSetting(Account account) {
        return Setting.builder()
                .account(account)
                .theme(Theme.LIGHT)
                .language(Language.EN)
                .motivationPerDay(2)
                .trackingMode(TrackingMode.AUTO_COUNTER)
                .reportDeadline(LocalTime.of(22, 0))
                .build();
    }
}
