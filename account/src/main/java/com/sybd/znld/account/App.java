package com.sybd.znld.account;

import com.mongodb.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.sybd.znld"})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
