package com.swpteam.smokingcessation.domain.dto.message;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest (

    @NotBlank(message = "MESSAGE_CONTENT_REQUIRED")
    String content
) {}
