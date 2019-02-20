package com.sybd.znld.onenet.dto;

public class CommandParams extends CommonParams{
    public String command;

    public CommandParams(Integer deviceId, String imei, OneNetKey oneNetKey, Integer timeout, String command){
        super(deviceId, imei, oneNetKey.getObjId(), oneNetKey.getResId(), oneNetKey.getObjInstId(), timeout);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
