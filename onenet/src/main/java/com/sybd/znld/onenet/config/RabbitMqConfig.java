package com.sybd.znld.onenet.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.onenet.service.IMessageService;
import com.sybd.znld.onenet.websocket.handler.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sybd.znld.service.onenet.IOneNetService;

@Slf4j
@Configuration
public class RabbitMqConfig {
    private final ObjectMapper objectMapper;
    private final IMessageService messageService;

    @Autowired
    public RabbitMqConfig(ObjectMapper objectMapper,
                          IMessageService messageService) {
        this.messageService = messageService;
        this.objectMapper = objectMapper;
    }

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

    @RabbitListener(queues = IOneNetService.ONENET_UP_MSG_ONLINE_QUEUE)
    public void receiveOnOffLineMessage(Message message) {
        var body = message.getBody();
        log.info("收到上下线消息: {}", new String(body));
        var news = this.messageService.onOffLine(new String(body));
        if(news != null) {
            try {
                OnOffLineHandler.sendAll(this.objectMapper.writeValueAsString(news)); // 推送消息
            }catch (JsonProcessingException ignored) { }
        }
    }

    @RabbitListener(queues = IOneNetService.ONENET_UP_MSG_ONOFF_QUEUE)
    public void receiveOnOffMessage(Message message) {
        var body = message.getBody();
        log.info("收到电源开关状态消息: {}", new String(body));
        var news = this.messageService.onOff(new String(body));
        if(news != null) {
            try {
                OnOffHandler.sendAll(this.objectMapper.writeValueAsString(news)); // 推送消息
            } catch (JsonProcessingException ignored) { }
        }
    }

    @RabbitListener(queues = IOneNetService.ONENET_UP_MSG_POSITION_QUEUE)
    public void receivePositionMessage(Message message) {
        var body = message.getBody();
        log.info("收到定位消息: {}", new String(body));
        var news = this.messageService.position(new String(body));
        if(news != null) {
            try {
                PositionHandler.sendAll(this.objectMapper.writeValueAsString(news)); // 推送消息
            } catch (JsonProcessingException ignored) { }
        }
    }

    @RabbitListener(queues = IOneNetService.ONENET_UP_MSG_ANGLE_QUEUE)
    public void receiveAngleMessage(Message message) {
        var body = message.getBody();
        log.info("收到倾斜消息: {}", new String(body));
        var news = this.messageService.angle(new String(body));
        if(news != null) {
            try {
                AngleHandler.sendAll(this.objectMapper.writeValueAsString(news)); // 推送消息
            } catch (JsonProcessingException ignored) { }
        }
    }

    @RabbitListener(queues = IOneNetService.ONENET_UP_MSG_ENVIRONMENT_QUEUE)
    public void receiveEnvironmentMessage(Message message) {
        var body = message.getBody();
        log.info("收到环境消息: {}", new String(body));
        var news = this.messageService.environment(new String(body));
        if (news != null) {
            try {
                EnvironmentHandler.sendAll(this.objectMapper.writeValueAsString(news)); // 推送消息
            } catch (JsonProcessingException ignored) { }
        }
    }

    @RabbitListener(queues = IOneNetService.ONENET_UP_MSG_LIGHT_QUEUE)
    public void receiveLightMessage(Message message) {
        var body = message.getBody();
        log.info("收到单灯消息: {}", new String(body));
        var statistics = this.messageService.statistics(new String(body));
        if(statistics != null) {
            try {
                LampStatisticsHandler.sendAll(this.objectMapper.writeValueAsString(statistics)); // 推送消息
            } catch (JsonProcessingException ignored) { }
        }
    }

    @RabbitListener(queues = IOneNetService.ONENET_ALARM_MSG_LIGHT_QUEUE)
    public void receiveAlarmMessage(Message message) {
        var body = message.getBody();
        var msg = new String(body);
        log.info("收到报警消息: {}", msg);
        LampAlarmHandler.sendAll(msg); // 推送消息
    }
}
