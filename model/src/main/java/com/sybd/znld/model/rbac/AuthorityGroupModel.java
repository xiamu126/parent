package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IValidForDbInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;

import java.io.Serializable;
import java.util.UUID;

public class AuthorityGroupModel implements Serializable, IValidForDbInsert {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String name;
    public String organizationId;
    public Status status = Status.OK;

    @Override
    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(MyString.isEmptyOrNull(name)) return false;
        return MyString.isUuid(organizationId);
    }
}
