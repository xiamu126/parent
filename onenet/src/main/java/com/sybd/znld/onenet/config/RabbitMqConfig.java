package com.sybd.znld.onenet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.sybd.znld.onenet.service.IMessageService;
import com.sybd.znld.onenet.websocket.handler.*;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sybd.znld.service.onenet.IOneNetService;
import java.io.IOException;

@Slf4j
@Configuration
public class RabbitMqConfig {
    @Bean
    public Declarables fanoutBindings() {
        var topicExchange = new TopicExchange(IOneNetService.ONENET_TOPIC_EXCHANGE);
        var onlineQueue = new Queue(IOneNetService.ONENET_UP_MSG_ONLINE_QUEUE, true);
        var onOffQueue = new Queue(IOneNetService.ONENET_UP_MSG_ONOFF_QUEUE, true);
        var positionQueue = new Queue(IOneNetService.ONENET_UP_MSG_POSITION_QUEUE, true);
        var angleQueue = new Queue(IOneNetService.ONENET_UP_MSG_ANGLE_QUEUE, true);
        var environmentQueue = new Queue(IOneNetService.ONENET_UP_MSG_ENVIRONMENT_QUEUE, true);
        var lightQueue = new Queue(IOneNetService.ONENET_UP_MSG_LIGHT_QUEUE, true);
        var alarmQueue = new Queue(IOneNetService.ONENET_ALARM_MSG_LIGHT_QUEUE, true);
        return new Declarables(topicExchange,
                onlineQueue, onOffQueue, positionQueue, angleQueue, environmentQueue, lightQueue,alarmQueue,
                BindingBuilder.bind(onlineQueue).to(topicExchange).with(IOneNetService.ONENET_UP_MSG_ONLINE_ROUTING_KEY),
                BindingBuilder.bind(onOffQueue).to(topicExchange).with(IOneNetService.ONENET_UP_MSG_ONOFF_ROUTING_KEY),
                BindingBuilder.bind(positionQueue).to(topicExchange).with(IOneNetService.ONENET_UP_MSG_POSITION_ROUTING_KEY),
                BindingBuilder.bind(angleQueue).to(topicExchange).with(IOneNetService.ONENET_UP_MSG_ANGLE_ROUTING_KEY),
                BindingBuilder.bind(environmentQueue).to(topicExchange).with(IOneNetService.ONENET_UP_MSG_ENVIRONMENT_ROUTING_KEY),
                BindingBuilder.bind(lightQueue).to(topicExchange).with(IOneNetService.ONENET_UP_MSG_LIGHT_ROUTING_KEY),
                BindingBuilder.bind(alarmQueue).to(topicExchange).with(IOneNetService.ONENET_ALARM_MSG_LIGHT_ROUTING_KEY)
        );
    }
}
