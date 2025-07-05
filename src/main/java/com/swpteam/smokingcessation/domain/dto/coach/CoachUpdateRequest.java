package com.swpteam.smokingcessation.domain.dto.coach;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CoachUpdateRequest(

        String fullName,

        @Email(message = "INVALID_EMAIL_FORMAT") String email,

        @Pattern(regexp = "\\d{10}", message = "INVALID_PHONE_NUMBER") String phoneNumber,

        @Size(max = 255, message = "COACH_BIO_TOO_LONG")
        String bio,

        @Min(value = 1, message = "COACH_EXPERIENCE_YEARS_MUST_BE_NON_NEGATIVE")
        int experienceYears,

        String socialLinks,

        String specializations,

        String certificates
) {
}
