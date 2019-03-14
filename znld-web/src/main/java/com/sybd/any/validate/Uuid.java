package com.sybd.any.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UuidValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Uuid {
    boolean required() default true;
    String message() default "Invalid uuid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
