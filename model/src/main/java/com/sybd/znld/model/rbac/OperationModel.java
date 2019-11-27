package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IValidForDBInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;

import java.util.UUID;

public class OperationModel implements IValidForDBInsert {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String name;
    public String path;
    public String resource;
    public String organizationId;
    public String app;
    public OperationType type;
    public Status status = Status.AUTHORITY_OPERATION_ALLOW;

    @Override
    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(MyString.isEmptyOrNull(name)) return false;
        if(MyString.isEmptyOrNull(path)) return false;
        if(MyString.isEmptyOrNull(resource)) return false;
        if(!MyString.isUuid(organizationId)) return false;
        if(MyString.isEmptyOrNull(app)) return false;
        return Status.isValidAuthorityCode(status.getValue());
    }
}
