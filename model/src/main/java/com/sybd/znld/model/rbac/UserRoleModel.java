package com.sybd.znld.model.rbac;

import com.sybd.znld.util.MyString;

import java.io.Serializable;

public class UserRoleModel implements Serializable {
    public String id;
    public String userId;
    public String roleId;
    public Integer status = Status.OK.getValue();

    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(!MyString.isUuid(userId)) return false;
        if(!MyString.isUuid(roleId)) return false;
        Status tmp = Status.getStatus(status);
        if(tmp == null) return false;
        return tmp == Status.OK;
    }
}
