package com.sybd.znld.position.websocket;

import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.springframework.web.socket.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Slf4j
public class PositioningWebSocketHandler implements WebSocketHandler {
    private static final ArrayList<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        /*var msg = message.getPayload().toString();
        if(msg.equals("data")){
            var file = ResourceUtils.getFile("classpath:data.log");
            var in = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            var br = new BufferedReader(in);
            var stringBuilder = new StringBuilder();
            String s = "data;";
            while ((s=br.readLine())!=null){
                stringBuilder.append(s);
            }
            session.sendMessage(new TextMessage(stringBuilder.toString()));
        }*/
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
                synchronized (PositioningWebSocketHandler.class){ // 如果不加锁，当多次调用这个sendAll，会出现上一次消息发送到一半，又要发新的消息
                    session.sendMessage(message);
                }
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
