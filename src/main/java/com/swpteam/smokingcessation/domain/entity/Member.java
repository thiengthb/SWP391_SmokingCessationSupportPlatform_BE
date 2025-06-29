package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.domain.enums.MemberGender;
import com.swpteam.smokingcessation.common.AuditableEntity;
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
public class Member extends AuditableEntity {

    @MapsId
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "accountId", nullable = false, updatable = false)
    Account account;

    String fullName;
    LocalDate dob;
    String address;
    int score;
    int highestStreak;
    LocalDateTime lastCounterReset;

    @Enumerated(EnumType.STRING)
    MemberGender gender;

    public static Member getDefaultMember(Account account) {
        return Member.builder()
                .account(account)
                .fullName(null)
                .dob(LocalDate.of(2000, 1, 1))
                .address(null)
                .score(0)
                .lastCounterReset(null)
                .highestStreak(0)
                .gender(MemberGender.MALE)
                .build();
    }

}
