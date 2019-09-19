package com.sybd.znld.onenet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Bean
    public TaskScheduler taskScheduler(){
        var taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.initialize();
        return taskScheduler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(),"/onenet/data/news")
                .addInterceptors(new MyHandshakeInterceptor()).setAllowedOrigins("*"); //支持websocket 的访问链接
        //registry.addHandler(new MyWebSocketHandler(),"/com/veg").addInterceptors(new MyHandshakeInterceptor()).withSockJS(); //不支持websocket的访问链接
    }
}
