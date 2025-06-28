package com.swpteam.smokingcessation.domain.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "EMAIL_REQUIRED")
        @Email(message = "INVALID_EMAIL_FORMAT")
        String email,

        @NotBlank(message = "BLANK_INVALID")
        @Size(min = 8, message = "PASSWORD_INVALID")
        String password,

        @NotBlank(message = "BLANK_INVALID")
        @Size(min = 8, message = "PASSWORD_INVALID")
        String confirmPassword
) {
}
