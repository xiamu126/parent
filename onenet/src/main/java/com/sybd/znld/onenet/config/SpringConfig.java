package com.sybd.znld.onenet.config;

import com.sybd.znld.onenet.service.IMessageService;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.onenet.IOneNetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EnableAsync
@EnableScheduling
@Configuration
public class SpringConfig {
    @Bean
    @ConditionalOnProperty(value = "schedule.enabled", matchIfMissing = true, havingValue = "true")
    public MyScheduledTask scheduledJob(ILampService lampService,
                                        RedissonClient redissonClient,
                                        IOneNetService oneNetService,
                                        IMessageService messageService) {
        return new MyScheduledTask(lampService, redissonClient, oneNetService, messageService);
    }

    @Bean("restTemplate")
    public RestTemplate restTemplate(){
        int timeout = 30*1000;
        var config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        var client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        var httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
        return new RestTemplate(httpComponentsClientHttpRequestFactory);
    }

    @Bean("TaskThreadPool")
    public ThreadPoolTaskExecutor taskThreadPool() {
        var executor =new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("TaskThreadPool");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean("MqThreadPool")
    public ThreadPoolTaskExecutor mqThreadPool() {
        var executor =new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("MqThreadPool");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public ConnectionFactory connectionFactoryForProducer(@Value("${spring.rabbitmq.addresses}") String addresses,
                                                          @Value("${spring.rabbitmq.username}") String username,
                                                          @Value("${spring.rabbitmq.password}") String password,
                                                          @Value("${spring.rabbitmq.virtual-host}") String virtualHost) {
        var connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(addresses);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.SIMPLE);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        var containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(connectionFactory);
        containerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return containerFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setUsePublisherConnection(true);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.error("消息["+new String(message.getBody())+"]被退回，指定的交换器["+exchange+"]与路由键["+routingKey+"]上没有绑定的队列");
        });
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if(!ack) {
                var msg = "消息送达交换器后消费者没有确认";
                if(correlationData != null) {
                    msg += "，返回的消息为" + correlationData.getReturnedMessage();
                }
                if(cause != null) {
                    msg += "，返回的错误提示为"+cause;
                }
                log.error(msg);
            }
        });
        return rabbitTemplate;
    }
}
