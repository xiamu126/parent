package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IValidForDBInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
public class UserModel implements Serializable, IValidForDBInsert {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String name;
    public String password;
    public LocalDateTime lastLoginTime;
    public String lastLoginIp;
    public String organizationId;
    public UserStatus status = UserStatus.OK;

    @Override
    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(MyString.isEmptyOrNull(name)) return false;
        if(MyString.isEmptyOrNull(password)) return false;
        if(lastLoginTime != null) return false;
        if(lastLoginIp != null) return false;
        return MyString.isUuid(organizationId);
    }
}
