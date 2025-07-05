package com.swpteam.smokingcessation.domain.dto.coach;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CoachCreateRequest(
        
    @NotBlank(message = "COACH_FULL_NAME_REQUIRED")
    String fullName,

    @Size(max = 255, message = "COACH_BIO_TOO_LONG")
    String bio,

    @NotNull(message = "COACH_EXPERIENCE_YEARS_REQUIRED")
    @Min(value = 1, message = "COACH_EXPERIENCE_YEARS_MUST_BE_NON_NEGATIVE")
    Integer experienceYears,

    @NotBlank(message = "COACH_SOCIAL_LINK_REQUIRED")
    String socialLinks,

    @NotBlank(message = "COACH_SPECIALIZATION_REQUIRED")
    String specializations,

    @NotBlank(message = "COACH_CERTIFICATES_REQUIRED")
    String certificates
) {}