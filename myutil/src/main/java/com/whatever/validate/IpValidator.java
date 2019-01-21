package com.whatever.validate;

import lombok.var;
import org.apache.commons.validator.routines.InetAddressValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IpValidator extends baseValidator implements ConstraintValidator<Ip, String> {
    private boolean required;
    @Override
    public void initialize(Ip constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        var inetAddressValidator = new InetAddressValidator();
        if(required){
            return inetAddressValidator.isValidInet4Address(value);
        }
        if (value == null) return true;
        return inetAddressValidator.isValidInet4Address(value);
    }
}