package com.sybd.znld.light;

import com.sybd.znld.mapper.config.MyScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;

//@EnableTransactionManagement // 用于多数据源的事务回滚，实际测试不加也能回滚
@SpringBootApplication(scanBasePackages = {"com.sybd.znld"})
@MyScan(basePackage = {"com.sybd.znld"})
public class App {
    public static void main(String[] args) {
        var context = SpringApplication.run(App.class, args);
        /*var beanDefinitionNames = context.getBeanDefinitionNames();
        Arrays.stream(beanDefinitionNames).forEach(System.out::println);*/
    }
}
