package com.swpteam.smokingcessation.domain.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequest (

    @Email(message = "INVALID_EMAIL_FORMAT") String email,

    @Size(min = 8, message = "INVALID_PASSWORD") String password,

    @Pattern(regexp = "\\d{10}", message = "INVALID_PHONE_NUMBER") String phoneNumber
) {}