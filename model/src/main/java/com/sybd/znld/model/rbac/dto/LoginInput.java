package com.sybd.znld.model.rbac.dto;

import com.sybd.znld.util.MyString;
import com.sybd.znld.validate.Uuid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

public class LoginInput implements Serializable {
    public String user;
    public String password; // 两次md5加密
    public String captcha; // 验证码不一定会传

    public boolean isValid(){
        return !MyString.isAnyEmptyOrNull(user, password);
    }
}
