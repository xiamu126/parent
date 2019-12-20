package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LampAlarmOutput {
    public String id;
    @JsonProperty("lamp_id")
    public String lampId;
    @JsonProperty("lamp_name")
    public String lampName;
    public String content;
    public Long at;
    public String status;
    public String type;
    @JsonProperty("region_name")
    public String regionName;
}
