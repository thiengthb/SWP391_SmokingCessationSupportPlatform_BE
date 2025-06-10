package com.swpteam.smokingcessation.apis.member.dto;

import com.swpteam.smokingcessation.apis.member.enums.MemberGender;
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
    String fullName;
    LocalDate dob;
    String address;
    MemberGender gender;
    int score;
    int currentStreak;
    LocalDateTime lastCounterReset;
}
