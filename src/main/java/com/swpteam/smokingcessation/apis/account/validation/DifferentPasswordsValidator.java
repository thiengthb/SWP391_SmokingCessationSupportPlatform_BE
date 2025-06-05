package com.swpteam.smokingcessation.apis.account.validation;

import com.swpteam.smokingcessation.apis.account.dto.request.ChangePasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DifferentPasswordsValidator implements ConstraintValidator<DifferentPasswords, ChangePasswordRequest> {

    @Override
    public boolean isValid(ChangePasswordRequest changePasswordRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (changePasswordRequest == null) return true;
        if (changePasswordRequest.getOldPassword() == null || changePasswordRequest.getNewPassword() == null)
            return true;
        return !changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword());
    }
}
