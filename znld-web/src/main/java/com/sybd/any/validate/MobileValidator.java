package com.sybd.any.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class MobileValidator extends baseValidator implements ConstraintValidator<Mobile, String> {
    private boolean required;

    @Override
    public void initialize(Mobile constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        var pattern = Pattern.compile("1\\d{10}");
        return super.isValid(required, value, pattern);
    }
}
