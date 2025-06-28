package com.swpteam.smokingcessation.domain.dto.feedback;

import com.swpteam.smokingcessation.domain.enums.FeedbackType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FeedbackRequest (

    String comment,

    @Min(value = 1, message = "FEEDBACK_RATING_MIN")
    @Max(value = 5, message = "FEEDBACK_RATING_MAX")
    Integer rating,

    FeedbackType feedbackType
) {}
