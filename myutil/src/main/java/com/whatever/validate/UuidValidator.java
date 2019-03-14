package com.whatever.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class UuidValidator extends baseValidator implements ConstraintValidator<Uuid, String> {
    private boolean required;

    @Override
    public void initialize(Uuid constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null && this.required){
            return false;
        }
        var pattern = Pattern.compile("^[0-9a-zA-Z]{32}$");
        return super.isValid(required, value, pattern);
    }
}
