package com.sybd.znld.light.config;

import com.sybd.znld.light.service.IStrategyService;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.onenet.IOneNetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
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
    @Value("${app.name}")
    private String appName;

    private final RedissonClient redissonClient;

    @Autowired
    public SpringConfig(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @EventListener(value = {ContextRefreshedEvent.class}, condition = "#root.event.applicationContext.containsBean('appRegister')")
    public void onStart(ContextRefreshedEvent event){
        var map = this.redissonClient.getMap("apps");
        if(map.containsKey(this.appName)) {
            throw new RuntimeException("app["+this.appName+"]已经注册");
        } else {
            map.put(this.appName, this.appName);
        }
    }

    @EventListener(value = {ContextClosedEvent.class}, condition = "#root.event.applicationContext.containsBean('appRegister')")
    public void onClose(ContextClosedEvent event){
        var map = this.redissonClient.getMap("apps");
        map.remove(this.appName);
    }

    @Bean(value = "appRegister")
    @ConditionalOnProperty(value = "app.register", matchIfMissing = true, havingValue = "true")
    public Boolean register() {
        return true;
    }


    @Bean
    @ConditionalOnProperty(value = "schedule.enabled", matchIfMissing = true, havingValue = "true")
    public MyScheduledTask scheduledJob(ILampService lampService,
                                        RedissonClient redissonClient,
                                        IOneNetService oneNetService,
                                        IStrategyService strategyService) {
        return new MyScheduledTask(lampService, redissonClient, oneNetService, strategyService);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder.failOnUnknownProperties(true);
        };
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

    @Bean("OneNetThreadPool")
    public ThreadPoolTaskExecutor myTaskAsyncPool() {
        var executor =new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("OneNetThreadPool");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean("TaskThreadPool")
    public ThreadPoolTaskExecutor myTaskAsyncPool2() {
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
}
