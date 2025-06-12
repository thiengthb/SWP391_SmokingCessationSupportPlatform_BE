package com.swpteam.smokingcessation.apis.member.dto.request;

import com.swpteam.smokingcessation.apis.member.enums.MemberGender;
import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberUpdateRequest {
    String fullName;
    @Past(message = "DOB_INVALID")
    LocalDate dob;
    String address;
    MemberGender gender;
}
