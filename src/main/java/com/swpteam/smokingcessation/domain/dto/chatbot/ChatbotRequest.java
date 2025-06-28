package com.swpteam.smokingcessation.domain.dto.chatbot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatbotRequest (

    @NotBlank(message = "PROMPT_REQUIRED")
    @Size(max = 200, message = "PROMPT_TOO_LONG")
    String prompt
) {}
