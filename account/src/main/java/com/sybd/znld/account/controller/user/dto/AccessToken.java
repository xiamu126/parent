package com.sybd.znld.account.controller.user.dto;

import org.codehaus.jackson.annotate.JsonProperty;

public class AccessToken {
    @JsonProperty("access_token")
    public String access_token;
    @JsonProperty("token_type")
    public String token_type;
    @JsonProperty("refresh_token")
    public String refresh_token;
    @JsonProperty("expires_in")
    public Integer expires_in;
    public String scope;
}
