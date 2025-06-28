package com.swpteam.smokingcessation.domain.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest (
    @NotBlank(message = "ACCOUNT_REQUIRED")
    String accountId,

    @NotBlank(message = "CHAT_MESSAGE_REQUIRED")
    @Size(max = 200, message = "CHAT_MESSAGE_TOO_LONG")
    String content
) {}
