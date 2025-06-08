package com.swpteam.smokingcessation.apis.subscription.entity;

import com.swpteam.smokingcessation.apis.account.entity.Account;
import com.swpteam.smokingcessation.apis.membership.entity.Membership;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String membershipName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String paymentStatus;

    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "membershipId")
    private Membership membership;
}
