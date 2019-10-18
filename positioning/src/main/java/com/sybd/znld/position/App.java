package com.sybd.znld.position;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@EnableRabbit
@SpringBootApplication(scanBasePackages = {"com.sybd.znld"})
public class App implements ApplicationContextAware {
    public static void main(String[] args)  {
        SpringApplication.run(App.class, args);
    }
    public static ApplicationContext ctx;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }
}
