package com.sybd.znld.security.oauth2.server.db;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DbSource {
    String value() default "znld";
}
