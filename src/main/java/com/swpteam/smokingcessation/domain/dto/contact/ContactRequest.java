package com.swpteam.smokingcessation.domain.dto.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContactRequest(
        @NotBlank(message = "NAME_REQUIRED")
        String name,
        @Email(message = "EMAIL_NOT_VALID")
        @NotBlank(message = "MAIL_REQUIRED")
        String email,
        @NotBlank(message = "MAIL_SUBJECT_REQUIRED")
        String subject,
        @NotBlank(message = "CONTENT_RESTRICTED")
        @Size(min = 10, message = "CONTENT_TOO_SHORT")
        @Size(max = 2000, message = "CONTENT_TOO_LONG")
        String content
) {
}
