package com.sybd.znld.mapper.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
//spring中的注解,加载对应的类
@Import(MyScannerRegister.class)//这个是我们的关键，实际上也是由这个类来扫描的
@Documented
public @interface MyScan {
    String[] basePackage() default {};
}
