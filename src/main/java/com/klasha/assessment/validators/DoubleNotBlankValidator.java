package com.klasha.assessment.validators;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DoubleNotBlankValidator implements ConstraintValidator<DoubleNotBlank, Double> {

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        return value != null && !value.isNaN();
    }
}