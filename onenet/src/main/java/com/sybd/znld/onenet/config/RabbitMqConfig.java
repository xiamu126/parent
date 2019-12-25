package com.sybd.znld.onenet.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sybd.znld.service.onenet.IOneNetService;

@Slf4j
@Configuration
public class RabbitMqConfig {
    @Bean
    public Declarables fanoutBindings() {
        var topicExchange = new TopicExchange(IOneNetService.ONENET_TOPIC_EXCHANGE);
        var onlineQueue = new Queue(IOneNetService.ONENET_ONLINE_UP_QUEUE, true);
        var onOffQueue = new Queue(IOneNetService.ONENET_ONOFF_UP_QUEUE, true);
        var positionQueue = new Queue(IOneNetService.ONENET_POSITION_UP_QUEUE, true);
        var angleQueue = new Queue(IOneNetService.ONENET_ANGLE_UP_QUEUE, true);
        var environmentQueue = new Queue(IOneNetService.ONENET_ENVIRONMENT_UP_QUEUE, true);
        var lightQueue = new Queue(IOneNetService.ONENET_LIGHT_UP_QUEUE, true);
        var alarmQueue = new Queue(IOneNetService.ONENET_LIGHT_ALARM_QUEUE, true);
        var executionQueue = new Queue(IOneNetService.ONENET_LIGHT_EXECUTION_QUEUE, true);
        return new Declarables(topicExchange,
                onlineQueue,
                onOffQueue,
                positionQueue,
                angleQueue,
                environmentQueue,
                lightQueue,
                alarmQueue,
                executionQueue,
                BindingBuilder.bind(onlineQueue).to(topicExchange).with(IOneNetService.ONENET_ONLINE_UP_ROUTING_KEY),
                BindingBuilder.bind(onOffQueue).to(topicExchange).with(IOneNetService.ONENET_ONOFF_UP_ROUTING_KEY),
                BindingBuilder.bind(positionQueue).to(topicExchange).with(IOneNetService.ONENET_POSITION_UP_ROUTING_KEY),
                BindingBuilder.bind(angleQueue).to(topicExchange).with(IOneNetService.ONENET_ANGLE_UP_ROUTING_KEY),
                BindingBuilder.bind(environmentQueue).to(topicExchange).with(IOneNetService.ONENET_ENVIRONMENT_UP_ROUTING_KEY),
                BindingBuilder.bind(lightQueue).to(topicExchange).with(IOneNetService.ONENET_LIGHT_UP_ROUTING_KEY),
                BindingBuilder.bind(alarmQueue).to(topicExchange).with(IOneNetService.ONENET_LIGHT_ALARM_ROUTING_KEY),
                BindingBuilder.bind(executionQueue).to(topicExchange).with(IOneNetService.ONENET_LIGHT_EXECUTION_ROUTING_KEY)
        );
    }
}
