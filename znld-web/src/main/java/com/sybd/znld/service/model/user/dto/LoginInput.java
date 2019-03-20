package com.sybd.znld.service.model.user.dto;

import com.whatever.validate.Uuid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

@ApiModel(value = "用户登入传入参数")
public class LoginInput {
    @ApiModelProperty(value = "会话Id")
    @Uuid(message = "UUID不能为空")
    public String uuid;
    @ApiModelProperty(value = "用户名")
    @NotEmpty(message = "用户名为空")
    public String user;
    @ApiModelProperty(value = "用户密码，两次md5加密")
    @NotEmpty(message = "密码为空")
    public String password;
    @ApiModelProperty(value = "验证码")
    @NotEmpty(message = "验证码为空")
    public String captcha;

    public LoginInput(){}
    public LoginInput(String uuid, @NotEmpty(message = "用户名为空") String user, @NotEmpty(message = "密码为空") String password, @NotEmpty(message = "验证码为空") String captcha) {
        this.uuid = uuid;
        this.user = user;
        this.password = password;
        this.captcha = captcha;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
