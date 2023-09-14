package com.klasha.assessment.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IntegerNotBlankValidator implements ConstraintValidator<IntegerNotBlank, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value!=null;
    }
}
