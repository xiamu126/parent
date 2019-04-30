package com.sybd.znld.znld.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {MobileValidator.class})
public @interface Mobile {
    boolean required() default true;
    String message() default "Invalid mobile";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
