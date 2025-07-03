package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.domain.enums.Language;
import com.swpteam.smokingcessation.domain.enums.MotivationFrequency;
import com.swpteam.smokingcessation.domain.enums.Theme;
import com.swpteam.smokingcessation.domain.enums.TrackingMode;
import com.swpteam.smokingcessation.common.AuditableEntity;
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
public class Setting extends AuditableEntity {

    @MapsId
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "accountId", nullable = false, updatable = false)
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

    Boolean changeFlag;

    public static Setting getDefaultSetting(Account account) {
        return Setting.builder()
                .account(account)
                .theme(Theme.SYSTEM)
                .language(Language.EN)
                .motivationFrequency(MotivationFrequency.DAILY)
                .trackingMode(TrackingMode.AUTO_COUNT)
                .reportDeadline(LocalTime.of(19, 0))
                .build();
    }
}
