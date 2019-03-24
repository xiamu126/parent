package com.sybd.znld.model.rbac;

import com.whatever.util.MyString;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter @Setter
public class UserModel implements Serializable {
    public String id;
    public String name;
    public String password;
    public String phone = "";
    public String email = "";
    public Short gender = 3;
    public Short age = 0;
    public String contactAddress = "";
    public String realName = "";
    public String idCardNo = "";
    public LocalDateTime lastLoginTime = LocalDateTime.now();
    public String lastLoginIp;
    public String organizationId;
    public Short status = 0;

    public boolean isValid(){
        return MyString.isUuid(id) && !MyString.isAnyEmptyOrNull(name, password) && MyString.isUuid(organizationId);
    }
}