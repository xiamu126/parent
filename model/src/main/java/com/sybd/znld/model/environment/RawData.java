package com.sybd.znld.model.environment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RawData implements Serializable {
    public String ds;
    public Object value;
    public LocalDateTime at;
    public Integer deviceId;
    public String imei;

    public List<String> getIds() {
        var ids = this.ds.split("_");
        if (ids.length != 3) {
            return null;
        }
        return Arrays.stream(ids).collect(Collectors.toList());
    }
}
