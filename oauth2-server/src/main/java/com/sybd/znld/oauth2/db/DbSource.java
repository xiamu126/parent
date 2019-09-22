package com.sybd.znld.oauth2.db;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DbSource {
    String value() default "znld";
}
