package com.sybd.znld.model.rbac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LogoutResult {
    @JsonProperty("user_id")
    public String userId;
}
