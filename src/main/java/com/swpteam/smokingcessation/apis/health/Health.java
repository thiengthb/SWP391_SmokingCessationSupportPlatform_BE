package com.swpteam.smokingcessation.apis.health;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.setting.Setting;
import com.swpteam.smokingcessation.apis.setting.enums.Language;
import com.swpteam.smokingcessation.apis.setting.enums.Theme;
import com.swpteam.smokingcessation.apis.setting.enums.TrackingMode;
import com.swpteam.smokingcessation.common.BaseEntity;
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
public class Health extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false)
    Account account;

    int cigarettesPerDay;
    int cigarettesPerPack;
    int fndLevel;
    double packPrice;
    String reasonToQuit;
    int smokeYear;

    public static Health getDefaultHealth(Account account) {
        return Health.builder()
                .account(account)
                .cigarettesPerDay(0)
                .cigarettesPerPack(0)
                .fndLevel(0)
                .packPrice(0f)
                .reasonToQuit("")
                .smokeYear(0)
                .build();
    }
}