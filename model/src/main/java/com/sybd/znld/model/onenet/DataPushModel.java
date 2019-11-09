package com.sybd.znld.model.onenet;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class DataPushModel {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public Integer deviceId;
    public String imei;
    public String datastreamId;
    public String name;
    public Object value;
    public LocalDateTime at;
    public Boolean locked = false;
}
