package com.sybd.znld.position.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.position.App;
import com.sybd.znld.position.model.Point;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sybd.znld.mapper.lamp.GpggaMapper;

@Slf4j
public class HistoryHandler implements WebSocketHandler {
    private static final ArrayList<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message){
        var gpggaMapper = App.ctx.getBean(GpggaMapper.class);
        var ret = gpggaMapper.selectByFilename(message.getPayload().toString());
        List<Point> points = new ArrayList<>();
        var maxCount = 10;
        var count = 0;
        if(ret != null){
            var lines = ret.content.split("\r\n");
            var lng = 0.0;
            var lat = 0.0;
            var mapper = new ObjectMapper();
            for(var line : lines){
                var tmp = line.split(",");
                try{
                    if(MyString.isEmptyOrNull(tmp[2]) || MyString.isEmptyOrNull(tmp[4])) continue;
                    var a = tmp[2].substring(0, 2);// 3109.5152754
                    var b = tmp[2].substring(2);
                    lat = Double.parseDouble(a) + (Double.parseDouble(b) / 60);
                    a = tmp[4].substring(0, 3);// 12038.8791150
                    b = tmp[4].substring(3);
                    lng = Double.parseDouble(a) + (Double.parseDouble(b) / 60);
                }catch (NumberFormatException | IndexOutOfBoundsException ex){
                    continue;
                }
                var point = new Point();
                point.lng = lng;
                point.lat = lat;
                points.add(point);
                log.debug(lng+","+lat);
                count++;
                if(count == maxCount){
                    try{
                        var result = mapper.writeValueAsString(points);
                        log.debug(result);
                        points.clear();
                        session.sendMessage(new TextMessage(result));
                        Thread.sleep(1000);
                    }catch (Exception ex){
                        log.error(ex.getMessage());
                        ex.printStackTrace();
                        try {
                            session.close(CloseStatus.SERVER_ERROR);
                        } catch (IOException ex2) {
                            log.error(ex2.getMessage());
                            ex2.printStackTrace();
                        }
                    }
                    count = 0;
                }
            }
        }
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
                synchronized (HistoryHandler.class){ // 如果不加锁，当多次调用这个sendAll，会出现上一次消息发送到一半，又要发新的消息
                    session.sendMessage(message);
                }
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
