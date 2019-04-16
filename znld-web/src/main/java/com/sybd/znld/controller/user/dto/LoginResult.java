package com.sybd.znld.controller.user.dto;

import com.sybd.znld.core.BaseApiResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@ApiModel(value = "登入返回数据")
public class LoginResult extends BaseApiResult{
    @ApiModelProperty(value = "登入后返回的用户Id")
    public String userId;
    @ApiModelProperty(value = "获取OAuth2的客户Id")
    public String clientId;
    @ApiModelProperty(value = "获取OAuth2的客户Secret")
    public String clientSecret;
    @ApiModelProperty(value = "OAuth2的过期时间")
    public Long auth2TokenExpirationTime;

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
}
