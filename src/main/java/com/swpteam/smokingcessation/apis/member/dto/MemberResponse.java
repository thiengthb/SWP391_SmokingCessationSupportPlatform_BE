package com.swpteam.smokingcessation.apis.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberResponse {
    String id;
    String fullName;
    LocalDate dob;
    String address;
    MemberGender gender;
    int score;
    int currentStreak;
    LocalDateTime lastCounterReset;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
