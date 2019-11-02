package com.sybd.znld.position.socket;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class Client {
    public static void main(String[] argv) throws Exception {
        var factory = new ConnectionFactory();
        factory.setHost("znld-rabbitmq");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin123");
        var connection = factory.newConnection();
        var channel = connection.createChannel();
        channel.exchangeDeclare("POSITIONING", BuiltinExchangeType.DIRECT);
        var queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "POSITIONING", "GPGGA");
        log.debug(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
