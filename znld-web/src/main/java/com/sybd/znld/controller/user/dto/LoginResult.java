package com.sybd.znld.controller.user.dto;

import com.sybd.znld.core.BaseApiResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "登入返回数据")
public class LoginResult extends BaseApiResult{
    @ApiModelProperty(value = "登入后返回的用户Id")
    private String userId;
    @ApiModelProperty(value = "获取OAuth2的客户Id")
    private String clientId;
    @ApiModelProperty(value = "获取OAuth2的客户Secret")
    private String clientSecret;
    @ApiModelProperty(value = "OAuth2的过期时间")
    private Long auth2TokenExpirationTime;

    public LoginResult(){}
    public LoginResult(Integer code, String msg){
        super(code, msg);
    }
    public LoginResult(Integer code, String msg, String userId, String clientId, String clientSecret, Long auth2TokenExpirationTime){
        super(code, msg);
        this.userId = userId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.auth2TokenExpirationTime = auth2TokenExpirationTime;
    }

    public static LoginResult fail(String msg){
        return new LoginResult(1, msg);
    }
    public static LoginResult success(String userId, String clientId, String clientSecret, Long auth2TokenExpirationTime){
        return new LoginResult(0, "", userId, clientId, clientSecret, auth2TokenExpirationTime);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Long getAuth2TokenExpirationTime() {
        return auth2TokenExpirationTime;
    }

    public void setAuth2TokenExpirationTime(Long auth2TokenExpirationTime) {
        this.auth2TokenExpirationTime = auth2TokenExpirationTime;
    }
}
