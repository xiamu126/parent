package com.sybd.znld.light;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;

//@EnableTransactionManagement // 用于多数据源的事务回滚，实际测试不加也能回滚
@SpringBootApplication(scanBasePackages = {"com.sybd.znld"})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
