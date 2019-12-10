package com.sybd.znld.onenet.websocket.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class LampStatisticsHandler implements WebSocketHandler {
    private static final ArrayList<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message){
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
        }
        sessions.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public static void sendAll(String msg) {
        for(var session : sessions){
            var message = new TextMessage(msg);
            try {
                synchronized (LampStatisticsHandler.class){ // 如果不加锁，当多次调用这个sendAll，会出现上一次消息发送到一半，又要发新的消息
                    session.sendMessage(message);
                }
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
