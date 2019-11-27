package com.sybd.znld.light.control.dto;

import lombok.ToString;

import java.util.List;

@ToString
public class Strategy {
    public List<String> ids; // 可以是照明灯的id，也可以是配电箱的id
    public String name; // 策略的名称
    public Long from; // 时间统一以时间戳，这个时间戳里包含日和时
    public Long to;
}
