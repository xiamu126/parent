package com.sybd.znld.socket.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class WsController {
    private final Logger log = LoggerFactory.getLogger(WsController.class);
    @Autowired
    public WsController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(MyMessage message,
                             @Header("simpSessionId") String sessionId,
                             @Header("nativeHeaders") Object headers) throws Exception {
        log.debug("nativeHeaders:"+headers);
        log.debug("sessionId:"+sessionId);
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, ");
    }

    private final SimpMessagingTemplate messagingTemplate;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    public void greetingTo(@RequestBody MyMessage message){
        this.messagingTemplate.convertAndSendToUser(message.fromUserId, "/message", message);
    }
}
