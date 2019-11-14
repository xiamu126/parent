package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IValidForDBInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;

import java.io.Serializable;
import java.util.UUID;

public class AuthorityModel implements Serializable, IValidForDBInsert {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String name;
    public String organizationId;
    public Integer status = Status.OK.getValue();

    @Override
    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(MyString.isEmptyOrNull(name)) return false;
        if(!MyString.isUuid(organizationId)) return false;
        var tmp = Status.getStatus(status);
        if(tmp == null) return false;
        return tmp == Status.OK;
    }
}
