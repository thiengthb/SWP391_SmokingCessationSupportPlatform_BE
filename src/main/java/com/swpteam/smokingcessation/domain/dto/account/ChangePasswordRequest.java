package com.swpteam.smokingcessation.domain.dto.account;

import com.swpteam.smokingcessation.validation.DifferentPasswords;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@DifferentPasswords
public record ChangePasswordRequest (
        
    @NotBlank(message = "PASSWORD_REQUIRED") String oldPassword,

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, message = "INVALID_PASSWORD") String newPassword
) {}
