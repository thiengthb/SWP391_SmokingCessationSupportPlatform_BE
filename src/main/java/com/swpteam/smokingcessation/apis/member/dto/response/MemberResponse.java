package com.swpteam.smokingcessation.apis.member.dto.response;

import com.swpteam.smokingcessation.apis.member.enums.MemberGender;
import com.swpteam.smokingcessation.apis.account.entity.Account;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberResponse {
    String id;
    String accountId;
    String fullName;
    LocalDate dob;
    String address;
    MemberGender gender;
    int score;
    int currentStreak;
    LocalDateTime lastCounterReset;
}
