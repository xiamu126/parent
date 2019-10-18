package com.sybd.znld.position.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    public DirectExchange direct(){
        return new DirectExchange("POSITIONING", false, false);
    }
    @Bean
    public Queue anonymousQueue() {
        return new AnonymousQueue();
    }
    @Bean
    public Binding binding(DirectExchange direct, Queue anonymousQueue) {
        return BindingBuilder.bind(anonymousQueue).to(direct).with("GPGGA");
    }
}
