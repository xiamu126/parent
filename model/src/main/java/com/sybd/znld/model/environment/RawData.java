package com.sybd.znld.model.environment;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RawData implements Serializable {
    public String ds;
    public Object value;
    public LocalDateTime at;
    public Integer deviceId;
    public String imei;
}
