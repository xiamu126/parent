package com.sybd.security.oauth2.server.db;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DbSource {
    String value() default "oauth";
}
