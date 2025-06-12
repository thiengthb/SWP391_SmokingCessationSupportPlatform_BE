package com.swpteam.smokingcessation.apis.account;

import com.swpteam.smokingcessation.apis.account.enums.AccountStatus;
import com.swpteam.smokingcessation.apis.account.enums.Role;
import com.swpteam.smokingcessation.apis.member.Member;
import com.swpteam.smokingcessation.apis.setting.Setting;
import com.swpteam.smokingcessation.apis.subscription.Subscription;
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

    private String username;

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

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, optional = false)
    Setting setting;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    Member member;
}