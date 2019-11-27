package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IValidForDBInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;

import java.util.UUID;

public class AuthorityAuthorityGroupModel implements IValidForDBInsert {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String authorityId;
    public String authorityGroupId;
    public Status status = Status.OK;

    @Override
    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(!MyString.isUuid(authorityId)) return false;
        return MyString.isUuid(authorityGroupId);
    }
}
