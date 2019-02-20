package com.sybd.znld.dto;

import java.io.Serializable;

public class MyMqMessage implements Serializable{
    public String msg;

    public MyMqMessage(){}
    public MyMqMessage(String msg){
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}