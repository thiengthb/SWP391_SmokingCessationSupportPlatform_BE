package com.swpteam.smokingcessation.domain.dto.chatbot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatbotRequest (

    @NotBlank(message = "PROMPT_NOT_BLANK")
    @Size(max = 200, message = "PROMPT_MAX_SIZE")
    String prompt
) {}
