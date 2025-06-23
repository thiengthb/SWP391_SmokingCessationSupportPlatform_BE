package com.swpteam.smokingcessation.domain.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequest (

    @Email(message = "EMAIL_INVALID") String email,

    @Size(min = 8, message = "PASSWORD_INVALID") String password,

    @Pattern(regexp = "\\d{10}", message = "PHONE_NUMBER_INVALID") String phoneNumber
) {}