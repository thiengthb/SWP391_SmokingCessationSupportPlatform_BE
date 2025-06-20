package com.swpteam.smokingcessation.domain.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest (
        
    @NotBlank(message = "RESET_TOKEN_REQUIRED")
    String token,

    @NotBlank(message = "MESSAGE_REQUIRED")
    @Size(min = 8, message = "PASSWORD_INVALID")
    String newPassword
) {}
