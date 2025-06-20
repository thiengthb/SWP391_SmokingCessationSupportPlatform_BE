package com.swpteam.smokingcessation.domain.dto.member;

import com.swpteam.smokingcessation.domain.enums.MemberGender;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record MemberRequest (

    String fullName,
    
    @Past(message = "DOB_INVALID")
    LocalDate dob,

    String address,

    @NotNull(message = "GENDER_REQUIRED")
    MemberGender gender
) {}
