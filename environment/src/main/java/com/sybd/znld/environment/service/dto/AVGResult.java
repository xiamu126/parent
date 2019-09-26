package com.sybd.znld.environment.service.dto;

import com.sybd.znld.util.MyDateTime;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
public class AVGResult {
    public Float value;
    public String describe;
    public Long at = MyDateTime.toTimestamp(LocalDateTime.now());
}
