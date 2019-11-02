package com.sybd.znld.position.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new PositioningWebSocketHandler(),"/positioning/data/news")
                .addInterceptors(new MyHandshakeInterceptor()).setAllowedOrigins("*"); //支持websocket 的访问链接
        registry.addHandler(new HistoryHandler(),"/positioning/data/history").setAllowedOrigins("*"); //支持websocket 的访问链接
       /* registry.addHandler(new PositioningWebSocketHandler(),"/positioning/data/news")
                .addInterceptors(new MyHandshakeInterceptor()).withSockJS(); //不支持websocket的访问链接*/
    }
}
