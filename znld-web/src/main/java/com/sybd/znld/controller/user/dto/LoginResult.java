package com.sybd.znld.controller.user.dto;

import com.sybd.znld.core.BaseApiResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@ApiModel(value = "登入返回数据")
public class LoginResult extends BaseApiResult {
    @ApiModelProperty(value = "登入后返回的用户Id")
    private String userId;
    @ApiModelProperty(value = "获取OAuth2的客户Id")
    private String clientId;
    @ApiModelProperty(value = "获取OAuth2的客户Secret")
    private String clientSecret;
    @ApiModelProperty(value = "OAuth2的过期时间")
    private Long auth2TokenExpirationTime;

    public static LoginResult fail(String msg){
        var tmp = new LoginResult();
        tmp.setCode(1);
        tmp.setMsg(msg);
        return tmp;
    }

    public static LoginResult success(String userId, String clientId, String clientSecret, Long auth2TokenExpirationTime){
        var tmp = new LoginResult();
        tmp.setCode(0);
        tmp.setUserId(userId);
        tmp.setClientId(clientId);
        tmp.setClientSecret(clientSecret);
        tmp.setAuth2TokenExpirationTime(auth2TokenExpirationTime);
        return tmp;
    }
}
