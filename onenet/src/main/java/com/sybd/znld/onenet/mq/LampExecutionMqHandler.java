package com.sybd.znld.onenet.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.sybd.znld.onenet.service.IMessageService;
import com.sybd.znld.onenet.websocket.handler.LampWsHandler;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LampExecutionMqHandler {
    private final ObjectMapper objectMapper;
    private final IMessageService messageService;

    @Autowired
    public LampExecutionMqHandler(ObjectMapper objectMapper, IMessageService messageService) {
        this.objectMapper = objectMapper;
        this.messageService = messageService;
    }

    @Async("MqThreadPool")
    @RabbitListener(queues = IOneNetService.ONENET_LIGHT_EXECUTION_QUEUE)
    public void handler(Channel channel, Message message) {
        try {
            var body = message.getBody();
            var msg = new String(body);
            log.info("收到路灯状态消息: {}", msg);
            if(!MyString.isEmptyOrNull(msg)) {
                LampWsHandler.sendAll(msg); // 推送消息
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 手动确认
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            // 发生了异常，拒绝此消息，并且丢弃此消息，即不再重新放入队列
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException ex2) {
                log.error(ex2.getMessage());
                log.error(ExceptionUtils.getStackTrace(ex2));
            }
        }
    }
}
