package com.sybd.znld.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication(scanBasePackages = {"com.sybd.znld"})
public class App {
    public static void main(String[] args){
        SpringApplication.run(App.class, args);
    }
}
