package com.swpteam.smokingcessation.domain.dto.member;

import com.swpteam.smokingcessation.domain.enums.MemberGender;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MemberCreateRequest(

    String fullName,
    
    @Past(message = "INVALID_DOB")
    LocalDate dob,

    @Size(max = 255, message = "ADDRESS_TOO_LONG")
    String address,

    @Size(max = 255, message = "BIO_TOO_LONG")
    String bio,

    @NotNull(message = "GENDER_REQUIRED")
    MemberGender gender
) {}
