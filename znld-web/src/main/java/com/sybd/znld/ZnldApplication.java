package com.sybd.znld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.sybd.znld"})
public class ZnldApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZnldApplication.class, args);
    }
}
