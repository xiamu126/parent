package com.sybd.znld.account.controller.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(value = "登入多次错误返回数据")
public class NeedCaptchaResult {
    @ApiModelProperty(value = "是否需要启用验证码")
    public Boolean needCaptcha;
}
