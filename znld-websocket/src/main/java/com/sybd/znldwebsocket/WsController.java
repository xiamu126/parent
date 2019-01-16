package com.sybd.znldwebsocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

@Slf4j
@Controller
public class WsController {
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
        this.messagingTemplate.convertAndSendToUser(message.getFromUserId(), "/message", message);
    }
}
