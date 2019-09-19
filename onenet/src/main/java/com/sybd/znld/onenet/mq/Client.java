package com.sybd.znld.onenet.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;

@Slf4j
@Component
public class Client {
    //@JmsListener(destination = "mailbox", containerFactory = "myFactory")
    public void onMessage(Message message) {
        log.debug(message.toString());
    }
}
