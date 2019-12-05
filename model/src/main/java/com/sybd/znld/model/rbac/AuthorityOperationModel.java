package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IValidForDbInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;

import java.io.Serializable;
import java.util.UUID;

public class AuthorityOperationModel implements Serializable, IValidForDbInsert {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String authorityId;
    public String operationId;
    public Status status = Status.OK;

    @Override
    public boolean isValidForInsert() {
        if(!MyString.isUuid(id)) return false;
        if(!MyString.isUuid(authorityId)) return false;
        return MyString.isUuid(operationId);
    }
}
