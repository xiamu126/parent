package com.sybd.znld.lamp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableTransactionManagement // 用于多数据源的事务回滚，实际测试不加也能回滚
@SpringBootApplication(scanBasePackages = {"com.sybd.znld"})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
