package com.swpteam.smokingcessation.domain.dto.account;

import com.swpteam.smokingcessation.domain.enums.Role;
import jakarta.validation.constraints.*;

public record AccountRequest(

        @NotBlank(message = "EMAIL_REQUIRED")
        @Email(message = "INVALID_EMAIL_FORMAT") String email,

        @NotBlank(message = "PASSWORD_REQUIRED")
        @Size(min = 8, message = "INVALID_PASSWORD") String password,

        @Pattern(regexp = "\\d{10}", message = "INVALID_PHONE_NUMBER") String phoneNumber,

        @NotNull(message = "ROLE_REQUIRED") Role role
) {}