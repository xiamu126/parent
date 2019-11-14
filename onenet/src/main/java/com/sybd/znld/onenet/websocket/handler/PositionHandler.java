package com.sybd.znld.onenet.websocket.handler;

import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.springframework.web.socket.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Slf4j
public class PositionHandler implements WebSocketHandler {
    private static final ArrayList<WebSocketSession> sessions = new ArrayList<>();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        /*var file = ResourceUtils.getFile("classpath:data.txt");
        var read = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        var bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            var tmp = lineTxt.split(",");
            System.out.println(tmp[2]+","+tmp[4]);
            if(MyString.isEmptyOrNull(tmp[2]) || MyString.isEmptyOrNull(tmp[4])) continue;
            var a = tmp[2].substring(0, 2);// 3109.5152754
            var b = tmp[2].substring(2);
            System.out.println(a+","+b);
            var lat = Double.parseDouble(a) + (Double.parseDouble(b) / 60);
            a = tmp[4].substring(0, 3);// 12038.8791150
            b = tmp[4].substring(3);
            System.out.println(a+","+b);
            var lng = Double.parseDouble(a) + (Double.parseDouble(b) / 60);
            System.out.println(lng+", "+lat);
            var message = new TextMessage(lng+","+lat);
            session.sendMessage(message);
            Thread.sleep(1000);
        }
        bufferedReader.close();
        read.close();*/
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

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

    public static void sendAll(String jsonStr) {
        for(var session : sessions){
            var message = new TextMessage(jsonStr);
            try {
                synchronized (PositionHandler.class){ // 如果不加锁，当多次调用这个sendAll，会出现上一次消息发送到一般，又要发新的消息
                    session.sendMessage(message);
                }
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
