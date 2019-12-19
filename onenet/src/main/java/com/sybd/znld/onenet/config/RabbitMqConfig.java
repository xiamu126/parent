package com.sybd.znld.onenet.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMqConfig {
    public static final String ONENET_UP_MSG_ONLINE_QUEUE = "ONENET_UP_MSG_ONLINE_QUEUE";
    public static final String ONENET_UP_MSG_ONOFF_QUEUE = "ONENET_UP_MSG_ONOFF_QUEUE";
    public static final String ONENET_UP_MSG_POSITION_QUEUE = "ONENET_UP_MSG_POSITION_QUEUE";
    public static final String ONENET_UP_MSG_ANGLE_QUEUE = "ONENET_UP_MSG_ANGLE_QUEUE";
    public static final String ONENET_UP_MSG_ENVIRONMENT_QUEUE = "ONENET_UP_MSG_ENVIRONMENT_QUEUE";
    public static final String ONENET_UP_MSG_LIGHT_QUEUE = "ONENET_UP_MSG_LIGHT_QUEUE";
    public static final String ONENET_TOPIC_EXCHANGE = "ONENET_TOPIC_EXCHANGE";
    public static final String ONENET_UP_MSG_ONLINE_ROUTING_KEY = "ONENET.UPMSG.ONLINE";
    public static final String ONENET_UP_MSG_ONOFF_ROUTING_KEY = "ONENET.UPMSG.ONOFF";
    public static final String ONENET_UP_MSG_POSITION_ROUTING_KEY = "ONENET.UPMSG.POSITION";
    public static final String ONENET_UP_MSG_ANGLE_ROUTING_KEY = "ONENET.UPMSG.ANGLE";
    public static final String ONENET_UP_MSG_ENVIRONMENT_ROUTING_KEY = "ONENET.UPMSG.ENVIRONMENT";
    public static final String ONENET_UP_MSG_LIGHT_ROUTING_KEY = "ONENET.UPMSG.LIGHT";

    @Bean
    public Declarables fanoutBindings() {
        var topicExchange = new TopicExchange(ONENET_TOPIC_EXCHANGE);
        var onlineQueue = new Queue(ONENET_UP_MSG_ONLINE_QUEUE, true);
        var onoffQueue = new Queue(ONENET_UP_MSG_ONOFF_QUEUE, true);
        var positionQueue = new Queue(ONENET_UP_MSG_POSITION_QUEUE, true);
        var angleQueue = new Queue(ONENET_UP_MSG_ANGLE_QUEUE, true);
        var environmentQueue = new Queue(ONENET_UP_MSG_ENVIRONMENT_QUEUE, true);
        var lightQueue = new Queue(ONENET_UP_MSG_LIGHT_QUEUE, true);
        return new Declarables(topicExchange,
                onlineQueue, onoffQueue, positionQueue, angleQueue, environmentQueue, lightQueue,
                BindingBuilder.bind(onlineQueue).to(topicExchange).with(ONENET_UP_MSG_ONLINE_ROUTING_KEY),
                BindingBuilder.bind(onoffQueue).to(topicExchange).with(ONENET_UP_MSG_ONOFF_ROUTING_KEY),
                BindingBuilder.bind(positionQueue).to(topicExchange).with(ONENET_UP_MSG_POSITION_ROUTING_KEY),
                BindingBuilder.bind(angleQueue).to(topicExchange).with(ONENET_UP_MSG_ANGLE_ROUTING_KEY),
                BindingBuilder.bind(environmentQueue).to(topicExchange).with(ONENET_UP_MSG_ENVIRONMENT_ROUTING_KEY),
                BindingBuilder.bind(lightQueue).to(topicExchange).with(ONENET_UP_MSG_LIGHT_ROUTING_KEY)
        );
    }

    @RabbitListener(queues = ONENET_UP_MSG_ONLINE_QUEUE)
    public void receiveOnlineMessage(Message message) {
        var body = message.getBody();
        log.info("收到上下线消息: {}", new String(body));
    }

    @RabbitListener(queues = ONENET_UP_MSG_ONOFF_QUEUE)
    public void receiveOnoffMessage(Message message) {
        var body = message.getBody();
        log.info("收到电源开关状态消息: {}", new String(body));
    }

    @RabbitListener(queues = ONENET_UP_MSG_POSITION_QUEUE)
    public void receivePositionMessage(Message message) {
        var body = message.getBody();
        log.info("收到定位消息: {}", new String(body));
    }

    @RabbitListener(queues = ONENET_UP_MSG_ANGLE_QUEUE)
    public void receiveAngleMessage(Message message) {
        var body = message.getBody();
        log.info("收到倾斜消息: {}", new String(body));
    }

    @RabbitListener(queues = ONENET_UP_MSG_ENVIRONMENT_QUEUE)
    public void receiveEnvironmentMessage(Message message) {
        var body = message.getBody();
        log.info("收到环境消息: {}", new String(body));
    }

    @RabbitListener(queues = ONENET_UP_MSG_LIGHT_QUEUE)
    public void receiveLightMessage(Message message) {
        var body = message.getBody();
        log.info("收到单灯消息: {}", new String(body));
    }

}
