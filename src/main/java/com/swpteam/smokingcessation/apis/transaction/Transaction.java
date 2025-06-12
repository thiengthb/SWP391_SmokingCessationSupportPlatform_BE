package com.swpteam.smokingcessation.apis.transaction;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.setting.Setting;
import com.swpteam.smokingcessation.apis.setting.enums.Language;
import com.swpteam.smokingcessation.apis.setting.enums.Theme;
import com.swpteam.smokingcessation.apis.setting.enums.TrackingMode;
import com.swpteam.smokingcessation.apis.transaction.enums.TransactionStatus;
import com.swpteam.smokingcessation.apis.transaction.enums.TransactionType;
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
public class Transaction extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false)
    Account account;

    double amount;

    @Enumerated(EnumType.STRING)
    TransactionStatus status;

    @Enumerated(EnumType.STRING)
    TransactionType type;

    public static Transaction startTransaction(Account account) {
        return Transaction.builder()
                .account(account)
                .status(TransactionStatus.PENDING)
                .build();
    }
}
