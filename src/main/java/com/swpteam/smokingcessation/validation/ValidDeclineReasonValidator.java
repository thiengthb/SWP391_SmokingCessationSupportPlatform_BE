package com.swpteam.smokingcessation.validation;

import com.swpteam.smokingcessation.domain.dto.booking.BookingAnswerRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDeclineReasonValidator implements ConstraintValidator<ValidDeclineReason, BookingAnswerRequest> {

    @Override
    public boolean isValid(BookingAnswerRequest bookingAnswerRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (bookingAnswerRequest == null) return true;

        String reason = bookingAnswerRequest.declineReason();
        boolean isAccepted = bookingAnswerRequest.accepted();

        if (isAccepted && reason != null && !reason.trim().isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DECLINE_REASON_NOT_ALLOWED")
                    .addConstraintViolation();
            return false;
        }

        if (!isAccepted && (reason == null || reason.trim().isEmpty())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DECLINE_REASON_REQUIRED")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
