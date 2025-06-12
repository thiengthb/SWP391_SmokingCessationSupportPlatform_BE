package com.swpteam.smokingcessation.apis.subscription;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.membership.Membership;
import com.swpteam.smokingcessation.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false)
    Account account;

    @ManyToOne
    @JoinColumn(name = "membershipId", nullable = false)
    Membership membership;

    LocalDate startDate;
    LocalDate endDate;
}
