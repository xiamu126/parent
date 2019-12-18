package com.sybd.znld.model.lamp.dto;

import java.time.LocalDateTime;

public class Statistic {
    public String id;
    public Integer online;
    public Double avgOnline;
    public Integer fault;
    public Double avgFault;
    public Integer light;
    public Double avgLight;
    public Double electricity;
    public LocalDateTime updateTime;
}
