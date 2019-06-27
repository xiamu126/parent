package com.sybd.znld.model.lamp.dto;

public class LampStatus {
    public String lampId;
    public String lampName;
    public Integer deviceId;
    public Integer miniStarStatus; // 景观灯状态，0 开启，1 关闭
    public Integer iScreenStatus; // 互动屏状态，0 开启，1 关闭
    public Integer eScreenStatus; // 电子屏状态，0 开启，1 关闭
    public Integer fanStatus; // 风扇状态，0 开启，1 关闭
    public Integer alarmStatus; // 一键报警状态，0 开启，1 关闭
    public Integer apStatus; // ap & wifi 状态，0 开启，1 关闭
}
