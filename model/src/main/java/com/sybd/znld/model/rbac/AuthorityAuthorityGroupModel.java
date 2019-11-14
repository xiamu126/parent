package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IValidForDBInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;

import java.util.UUID;

public class AuthorityAuthorityGroupModel implements IValidForDBInsert {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String authorityId;
    public String authorityGroupId;
    public Integer status = Status.OK.getValue();

    @Override
    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(!MyString.isUuid(authorityId)) return false;
        if(!MyString.isUuid(authorityGroupId)) return false;
        Status tmp = Status.getStatus(status);
        if(tmp == null) return false;
        return tmp == Status.OK;
    }
}
