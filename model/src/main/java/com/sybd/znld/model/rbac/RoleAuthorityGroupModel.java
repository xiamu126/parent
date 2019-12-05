package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IValidForDbInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;

import java.io.Serializable;

public class RoleAuthorityGroupModel implements Serializable, IValidForDbInsert {
    public String id;
    public String roleId;
    public String authorityGroupId; // 权限组编号
    public Status status = Status.OK;

    @Override
    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(!MyString.isUuid(roleId)) return false;
        return MyString.isUuid(authorityGroupId);
    }
}
