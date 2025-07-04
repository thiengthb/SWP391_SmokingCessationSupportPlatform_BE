package com.swpteam.smokingcessation.domain.dto.member;

import com.swpteam.smokingcessation.domain.enums.MemberGender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record MemberUpdateRequest (

        String fullName,

        @Email(message = "INVALID_EMAIL_FORMAT") String email,

        @Pattern(regexp = "\\d{10}", message = "INVALID_PHONE_NUMBER") String phoneNumber,

        @Past(message = "INVALID_DOB") LocalDate dob,

        @Size(max = 255, message = "ADDRESS_TOO_LONG") String address,

        @Size(max = 255, message = "BIO_TOO_LONG") String bio,

        MemberGender gender
) {}
