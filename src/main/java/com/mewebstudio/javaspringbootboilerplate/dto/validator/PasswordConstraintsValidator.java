package com.mewebstudio.javaspringbootboilerplate.dto.validator;

import com.mewebstudio.javaspringbootboilerplate.dto.annotation.Password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public final class PasswordConstraintsValidator implements ConstraintValidator<Password, String> {
    @Override
    public void initialize(final Password constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {
        if (password == null) {
            return true;
        }
        return !password.trim().isEmpty();
    }
}
