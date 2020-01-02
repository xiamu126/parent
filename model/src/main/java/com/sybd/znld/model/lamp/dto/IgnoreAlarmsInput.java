package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class IgnoreAlarmsInput {
    public List<String> ids;
    @JsonProperty("user_id")
    public String userId;
    @JsonProperty("organ_id")
    public String organId;
}
