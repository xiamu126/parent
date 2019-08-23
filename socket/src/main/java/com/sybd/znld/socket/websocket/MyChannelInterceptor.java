package com.sybd.znld.socket.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class MyChannelInterceptor implements ChannelInterceptor {
    private final Logger log = LoggerFactory.getLogger(MyChannelInterceptor.class);
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        var headers = message.getHeaders();
        var nh = headers.get("nativeHeaders");
        for(var item : headers.entrySet()){
            log.error(item.getKey()+" "+item.getValue());
        }
        System.out.println(command != null ? command.toString(): "null");
    }
}
