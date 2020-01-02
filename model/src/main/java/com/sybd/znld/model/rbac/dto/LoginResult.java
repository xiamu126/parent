package com.sybd.znld.model.rbac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class LoginResult implements Serializable {
    @JsonProperty("user_id")
    public String userId;
    @JsonProperty("organ_id")
    public String organId;
    public String token;
    @JsonProperty("token_expire")
    public Long tokenExpire;
    @JsonProperty("need_captcha")
    public Boolean needCaptcha;
}
