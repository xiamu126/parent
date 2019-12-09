package com.sybd.znld.light.config;

import com.sybd.znld.light.service.IStrategyService;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.onenet.IOneNetService;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@EnableScheduling
@Configuration
public class SpringConfig {
    @Bean
    @ConditionalOnProperty(value = "jobs.enabled", matchIfMissing = true, havingValue = "true")
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
