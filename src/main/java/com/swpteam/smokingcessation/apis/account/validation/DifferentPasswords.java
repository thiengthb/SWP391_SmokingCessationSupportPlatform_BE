package com.swpteam.smokingcessation.apis.account.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DifferentPasswordsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DifferentPasswords {
    String message() default "New password must be different from old password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
