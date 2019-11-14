package com.sybd.znld.onenet.websocket.config;

import com.sybd.znld.onenet.websocket.MyHandshakeInterceptor;
import com.sybd.znld.onenet.websocket.handler.AngleHandler;
import com.sybd.znld.onenet.websocket.handler.EnvironmentHandler;
import com.sybd.znld.onenet.websocket.handler.OnOffHandler;
import com.sybd.znld.onenet.websocket.handler.PositionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        registry.addHandler(new EnvironmentHandler(),"/data/news/environment")
                .addInterceptors(new MyHandshakeInterceptor()).setAllowedOrigins("*"); //支持websocket 的访问链接
        registry.addHandler(new PositionHandler(),"/data/news/position")
                .addInterceptors(new MyHandshakeInterceptor()).setAllowedOrigins("*");
        registry.addHandler(new AngleHandler(),"/data/news/angle")
                .addInterceptors(new MyHandshakeInterceptor()).setAllowedOrigins("*");
        registry.addHandler(new OnOffHandler(),"/data/news/onoff")
                .addInterceptors(new MyHandshakeInterceptor()).setAllowedOrigins("*");
    }
}
