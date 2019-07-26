package com.sybd.znld.model.ministar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class TwinkleHistory {
    public String id;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public LocalDateTime beginTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public LocalDateTime endTime;
    public String regionId;
    public String regionName;
    public String color;
    public Integer type;
    public Integer rate;
    public String userId;
    public String userName;
    public Integer status;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public LocalDateTime triggerTime;
    public String title;
}
