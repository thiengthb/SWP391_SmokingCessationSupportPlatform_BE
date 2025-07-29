package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.common.AuditableEntity;
import com.swpteam.smokingcessation.domain.enums.Currency;
import com.swpteam.smokingcessation.domain.enums.PaymentMethod;
import com.swpteam.smokingcessation.domain.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false, updatable = false)
    Account account;

    @ManyToOne
    @JoinColumn(name = "membershipId", nullable = false, updatable = false)
    Membership membership;

    double amount;

    @Enumerated(EnumType.STRING)
    Currency currency;

    @Enumerated(EnumType.STRING)
    TransactionStatus status;

    @Enumerated(EnumType.STRING)
    PaymentMethod method;

    public static Transaction startTransaction(Account account) {
        return Transaction.builder()
                .account(account)
                .status(TransactionStatus.PENDING)
                .build();
    }
}
