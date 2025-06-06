package com.swpteam.smokingcessation.apis.member.dto.request;

import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberCreateRequest {
    String fullName;
    @Past(message = "DOB_INVALID")
    LocalDate dob;
    String address;
    MemberGender gender;
}
