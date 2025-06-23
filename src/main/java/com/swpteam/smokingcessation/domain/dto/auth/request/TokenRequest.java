package com.swpteam.smokingcessation.domain.dto.auth.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest (

    @NotBlank(message = "TOKEN_REQUIRED")
    String token
) {}
