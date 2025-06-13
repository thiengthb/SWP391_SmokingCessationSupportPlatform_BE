package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.domain.enums.MemberGender;
import com.swpteam.smokingcessation.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Member extends BaseEntity {

    @OneToOne
    @MapsId
    @JoinColumn(name = "accountId")
    Account account;

    String fullName;
    LocalDate dob;
    String address;
    int score;
    int currentStreak;
    LocalDateTime lastCounterReset;

    @Enumerated(EnumType.STRING)
    MemberGender gender;

    public static Member getDefaultMember(Account account) {
        return Member.builder()
                .account(account)
                .fullName(null)
                .dob(null)
                .address(null)
                .gender(null)
                .score(0)
                .currentStreak(0)
                .lastCounterReset(null)
                .build();
    }
}
