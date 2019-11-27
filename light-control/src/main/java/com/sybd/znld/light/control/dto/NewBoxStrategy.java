package com.sybd.znld.light.control.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class NewBoxStrategy extends Strategy {
    @JsonProperty("user_id")
    public String userId; // 是谁新建这个配电箱策略的
}
