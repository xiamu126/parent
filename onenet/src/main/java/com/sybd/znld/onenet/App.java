package com.sybd.znld.onenet;

import com.sybd.znld.mapper.lamp.OneNetResourceMapper;
import com.sybd.znld.onenet.config.MyWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.sybd.znld"})
public class App {
    @Autowired
    public OneNetResourceMapper oneNetResourceMapper;

    public static void main(String[] args){
        SpringApplication.run(App.class, args);

    }

    //@Scheduled(fixedRate = 1000 * 5) //每隔30秒向客户端发送一次数据
    public void sendMessage() {
        //this.oneNetResourceMapper.selectNameByDataStreamId("3305","0","5577");
        MyWebSocketHandler.sendAll("test");
        //log.debug("发送消息完毕");
    }
}
