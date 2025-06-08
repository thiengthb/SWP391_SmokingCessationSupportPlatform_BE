package com.swpteam.smokingcessation.apis.member.entity;

import com.swpteam.smokingcessation.apis.account.entity.Account;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @OneToOne(fetch = FetchType.LAZY)
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
}
