package com.sybd.znld.model.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter @Setter
public class UserEntity implements Serializable {
    @NotEmpty(message = "Id不能为空")
    private String id;
    private String name;
    private String password;
    private String phone;
    private String email;
    private Short gender;
    private Short age;
    private String contactAddress;
    private String realName;
    private String idCardNo;
    private String lastLoginTime;
    private String lastLoginIp;
    private String authorities;
}
