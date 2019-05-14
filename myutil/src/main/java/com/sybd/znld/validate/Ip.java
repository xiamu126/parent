package com.sybd.znld.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IpValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ip {
    boolean required() default true;
    String message() default "Invalid IP";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
