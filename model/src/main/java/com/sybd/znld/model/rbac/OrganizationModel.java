package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IValidForDBInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
public class OrganizationModel implements Serializable, IValidForDBInsert {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String name;
    public Status status = Status.OK;
    public String oauth2ClientId;

    @Override
    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(MyString.isEmptyOrNull(name)) return false;
        return !MyString.isEmptyOrNull(oauth2ClientId);
    }
}
