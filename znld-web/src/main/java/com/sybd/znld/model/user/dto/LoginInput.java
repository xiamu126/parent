package com.sybd.znld.model.user.dto;

import com.whatever.validate.Uuid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@ApiModel(value = "用户登入传入参数")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor
public class LoginInput {
    @ApiModelProperty(value = "会话Id")
    @Uuid(message = "UUID不能为空")
    private String uuid;
    @ApiModelProperty(value = "用户名")
    @NotEmpty(message = "用户名为空")
    private String user;
    @ApiModelProperty(value = "用户密码，两次md5加密")
    @NotEmpty(message = "密码为空")
    private String password;
    @ApiModelProperty(value = "验证码")
    @NotEmpty(message = "验证码为空")
    private String captcha;
}
