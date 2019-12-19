package com.sybd.znld.light.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMqConfig {
    private static final String ONENET_UP_MSG_QUEUE = "ONENET_UP_MSG_QUEUE";
    private static final String ONENET_EXCHANGE = "ONENET_EXCHANGE";
    private static final String ONENET_UP_MSG_ROUTING_KEY = "ONENET_UP_MSG";

    @Bean
    public Declarables fanoutBindings() {
        var queue = new Queue(ONENET_UP_MSG_QUEUE, true);
        var directExchange = new DirectExchange(ONENET_EXCHANGE);
        return new Declarables(queue, directExchange, BindingBuilder.bind(queue).to(directExchange).with(ONENET_UP_MSG_ROUTING_KEY));
    }
}
