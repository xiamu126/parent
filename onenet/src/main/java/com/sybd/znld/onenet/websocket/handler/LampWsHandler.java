package com.sybd.znld.onenet.websocket.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class LampWsHandler implements WebSocketHandler {
    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            var msg = message.getPayload().toString();
            if (msg.equals("ping")) {
                session.sendMessage(new TextMessage("pong"));
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
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
        synchronized (LampWsHandler.class) { // 如果不加锁，当多次调用这个sendAll，会出现上一次消息发送到一半，又要发新的消息
            sessions.removeIf(s -> !s.isOpen());
            for (var session : sessions) {
                var message = new TextMessage(msg);
                try {
                    session.sendMessage(message);
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                    log.error(ExceptionUtils.getStackTrace(ex));
                }
            }
        }
    }
}
