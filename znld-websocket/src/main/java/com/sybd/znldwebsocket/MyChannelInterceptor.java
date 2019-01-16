package com.sybd.znldwebsocket;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyChannelInterceptor implements ChannelInterceptor {
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
