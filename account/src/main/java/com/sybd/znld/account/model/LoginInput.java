package com.sybd.znld.account.model;

import com.sybd.znld.util.MyString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@ApiModel(value = "用户登入传入参数")
public class LoginInput implements Serializable {
    @ApiModelProperty(value = "用户名")
    @NotEmpty(message = "用户名为空")
    public String user;
    @ApiModelProperty(value = "用户密码，两次md5加密")
    @NotEmpty(message = "密码为空")
    public String password;
    @ApiModelProperty(value = "验证码")
    @NotEmpty(message = "验证码为空")
    public String captcha;

    public boolean isValid(){
        return !MyString.isAnyEmptyOrNull(user, password, captcha);
    }
}
