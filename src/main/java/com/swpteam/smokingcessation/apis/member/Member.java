package com.swpteam.smokingcessation.apis.member;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.member.enums.MemberGender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Member {
    @Id
    String id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id")
    Account account;

    @Column(name = "full_name")
    String fullName;

    LocalDate dob;
    String address;
    @Enumerated(EnumType.STRING)
    MemberGender gender;
    int score;

    @Column(name = "current_streak")
    int currentStreak;
    @Column(name = "last_counter_reset")
    LocalDateTime lastCounterReset;

    public Member getDefaultMember() {
        return Member.builder()
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
