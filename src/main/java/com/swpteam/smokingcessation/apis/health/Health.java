package com.swpteam.smokingcessation.apis.health;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Health extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    Account account;

    int cigarettesPerDay;
    int cigarettesPerPack;
    int fndLevel;
    double packPrice;
    String reasonToQuit;
    int smokeYear;
}