package com.swpteam.smokingcessation.domain.dto.auth.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(

        @NotBlank(message = "CODE_REQUIRED")
        String idToken
) {}
