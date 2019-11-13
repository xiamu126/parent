package com.sybd.znld.model.rbac;

import com.sybd.znld.util.MyString;

import java.util.UUID;

public class AuthorityAuthorityGroupModel {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String authorityId;
    public String authorityGroupId;
    public Integer status = Status.OK.getValue();

    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(!MyString.isUuid(authorityId)) return false;
        if(!MyString.isUuid(authorityGroupId)) return false;
        Status tmp = Status.getStatus(status);
        if(tmp == null) return false;
        return tmp == Status.OK;
    }
}
