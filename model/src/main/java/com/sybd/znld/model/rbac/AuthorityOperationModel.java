package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IValidForDBInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;

import java.io.Serializable;
import java.util.UUID;

public class AuthorityOperationModel implements Serializable, IValidForDBInsert {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String authorityId;
    public String operationId;
    public Integer status;

    @Override
    public boolean isValidForInsert() {
        if(!MyString.isUuid(id)) return false;
        if(!MyString.isUuid(authorityId)) return false;
        if(!MyString.isUuid(operationId)) return false;
        var tmp = Status.getStatus(status);
        if(tmp == null) return false;
        return tmp == Status.OK;
    }
}
