package com.sybd.znld.model.environment;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor @AllArgsConstructor
public class RealTimeData implements Serializable {
    public Object value;
    public String describe;
    public Long at;

    public RealTimeData(Object value, Long at){
        this.value = value;
        this.at = at;
    }
}
