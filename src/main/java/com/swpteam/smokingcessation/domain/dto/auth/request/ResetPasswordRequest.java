package com.swpteam.smokingcessation.domain.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest (
        
    @NotBlank(message = "TOKEN_REQUIRED")
    String token,

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, message = "INVALID_PASSWORD")
    String newPassword
) {}
