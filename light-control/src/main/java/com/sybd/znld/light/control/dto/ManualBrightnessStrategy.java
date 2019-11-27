package com.sybd.znld.light.control.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import java.util.List;

@ToString
public class ManualBrightnessStrategy {
    public List<String> ids;
    public Integer brightness;
    @JsonProperty("user_id")
    public String userId;
}
