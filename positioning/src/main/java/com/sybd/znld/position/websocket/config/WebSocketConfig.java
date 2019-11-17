package com.sybd.znld.position.websocket.config;

import com.sybd.znld.position.websocket.handler.HistoryHandler;
import com.sybd.znld.position.websocket.MyHandshakeInterceptor;
import com.sybd.znld.position.websocket.handler.RealTimeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new RealTimeHandler(),"/data/news")
                .addInterceptors(new MyHandshakeInterceptor()).setAllowedOrigins("*"); //支持websocket 的访问链接
        registry.addHandler(new HistoryHandler(),"/data/history")
                .addInterceptors(new MyHandshakeInterceptor()).setAllowedOrigins("*"); //支持websocket 的访问链接
    }
}
