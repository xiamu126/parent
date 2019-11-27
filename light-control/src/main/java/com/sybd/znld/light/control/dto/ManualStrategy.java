package com.sybd.znld.light.control.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ManualStrategy {
    public List<String> ids;
    public Integer type; // 0=打开，1=关闭
    @JsonProperty("user_id")
    public String userId;
}
