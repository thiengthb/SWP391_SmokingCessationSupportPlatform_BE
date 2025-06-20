package com.swpteam.smokingcessation.validation;

import com.swpteam.smokingcessation.domain.dto.account.ChangePasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DifferentPasswordsValidator implements ConstraintValidator<DifferentPasswords, ChangePasswordRequest> {

    @Override
    public boolean isValid(ChangePasswordRequest changePasswordRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (changePasswordRequest == null) return true;
        if (changePasswordRequest.oldPassword() == null || changePasswordRequest.newPassword() == null)
            return true;
        return !changePasswordRequest.oldPassword().equals(changePasswordRequest.newPassword());
    }
}
