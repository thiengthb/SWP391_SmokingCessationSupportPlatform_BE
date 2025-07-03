package com.swpteam.smokingcessation.domain.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.domain.enums.MemberGender;
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
public class MemberProfileResponse {
    String id;
    String username;
    String fullName;
    String email;
    String phoneNumber;
    String address;
    MemberGender gender;
    LocalDate dob;
    String avatar;
    String bio;
    LocalDateTime createdAt;
}
