package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account extends BaseEntity {

    @Column(unique = true, columnDefinition = "NVARCHAR(30)")
    String username;

    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(30)")
    String email;

    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    String password;

    @Column(unique = true, columnDefinition = "NVARCHAR(10)")
    String phoneNumber;

    @Enumerated(EnumType.STRING)
    Role role;

    @Enumerated(EnumType.STRING)
    AccountStatus status;

    String avatar;

    @OneToMany(mappedBy = "account")
    List<Subscription> subscriptions;

    @OneToMany(mappedBy = "account")
    List<AITokenUsage> aiTokenUsages;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, optional = false)
    Setting setting;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    Member member;

    public boolean isHavingSubscription() {
        return subscriptions != null &&
                subscriptions.stream().anyMatch(Subscription::isActive);
    }
}