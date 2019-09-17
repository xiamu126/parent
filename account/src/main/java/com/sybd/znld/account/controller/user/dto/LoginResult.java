package com.sybd.znld.account.controller.user.dto;

import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.util.MyDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
@ApiModel(value = "登入返回数据")
public class LoginResult {
    @ApiModelProperty(value = "登入后返回的用户Id")
    public String userId;
    @ApiModelProperty(value = "登入后返回此用户所属组织Id")
    public String organId;
    @ApiModelProperty(value = "访问令牌")
    public String token;
    @ApiModelProperty(value = "令牌到期时间，时间戳")
    public Long tokenExpire;
    @ApiModelProperty(value = "服务器当前时间，时间戳")
    public Long now = MyDateTime.toTimestamp(LocalDateTime.now());
    @ApiModelProperty(value = "目录数据")
    public Object menu;
    @ApiModelProperty(value = "是否需要启用验证码")
    public Boolean needCaptcha;
}
