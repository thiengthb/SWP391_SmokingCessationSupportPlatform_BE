package com.swpteam.smokingcessation.domain.dto.review;

import jakarta.validation.constraints.*;

public record ReviewUpdateRequest (

    String comment,

    @Min(value = 1, message = "REVIEW_RATING_MIN")
    @Max(value = 5, message = "REVIEW_RATING_MAX")
    int rating
) {}
