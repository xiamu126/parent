package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IValidForDbInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;

import java.io.Serializable;

public class UserRoleModel implements Serializable, IValidForDbInsert {
    public String id;
    public String userId;
    public String roleId;
    public Status status = Status.OK;

    @Override
    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(!MyString.isUuid(userId)) return false;
        return MyString.isUuid(roleId);
    }
}
