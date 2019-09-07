package com.sybd.znld.socket.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    public WebSocketMessageBrokerConfig(MyHandshakeInterceptor myHandshakeInterceptor,
                                        MyHandshakeHandler myHandshakeHandler,
                                        MyChannelInterceptor myChannelInterceptor) {
        this.myHandshakeInterceptor = myHandshakeInterceptor;
        this.myHandshakeHandler = myHandshakeHandler;
        this.myChannelInterceptor = myChannelInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/message");
        config.setApplicationDestinationPrefixes("/ws");
        config.setUserDestinationPrefix("/my");
    }

    private final MyHandshakeInterceptor myHandshakeInterceptor;
    private final MyHandshakeHandler myHandshakeHandler;
    private final MyChannelInterceptor myChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/WsEndpoint")
                .addInterceptors(myHandshakeInterceptor)
                .setHandshakeHandler(myHandshakeHandler)
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(myChannelInterceptor);
    }
}
