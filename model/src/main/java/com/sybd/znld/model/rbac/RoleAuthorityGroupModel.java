package com.sybd.znld.model.rbac;

import com.sybd.znld.util.MyString;

import java.io.Serializable;

public class RoleAuthorityGroupModel implements Serializable {
    public String id;
    public String roleId;
    public String authorityGroupId; // 权限组编号
    public Integer status = Status.OK.getValue();

    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(!MyString.isUuid(roleId)) return false;
        if(!MyString.isUuid(authorityGroupId)) return false;
        Status tmp = Status.getStatus(status);
        if(tmp == null) return false;
        return tmp == Status.OK;
    }
}
