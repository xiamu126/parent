package com.sybd.znldwebsocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class MyMessage {
    private String fromUserId;
    private String msg;
}
