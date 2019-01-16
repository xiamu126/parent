package com.sybd.znld.onenet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommandParams extends CommonParams {
    private String command;

    public CommandParams(Integer deviceId, String imei, OneNetKey key, int timeout, String command){
        super(deviceId, imei, key, timeout);
        this.command = command;
    }
}
