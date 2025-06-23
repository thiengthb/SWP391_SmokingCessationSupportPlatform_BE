package com.swpteam.smokingcessation.domain.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewCreateRequest(

        @NotBlank(message = "REVIEW_COACH_ID_REQUIRED")
        String coachId,

        String comment,

        @Min(value = 1, message = "REVIEW_RATING_MIN")
        @Max(value = 5, message = "REVIEW_RATING_MAX")
        int rating
) {
}
