package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IValid;
import com.sybd.znld.znld.util.MyString;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class OrganizationModel implements IValid, Serializable {
    public String id;
    public String name;
    public String parentId = "";
    public Integer position = 0;
    public Short status = Status.OK;
    public String oauth2ClientId;

    @Override
    public boolean isValid() {
        if(MyString.isEmptyOrNull(name)) return false;
        // 如果是顶级节点，则position必须为0
        if(parentId.equals("") && position != 0) return false;
        if(!MyString.isUuid(parentId)) return false;
        return
                Status.isValid(status) &&
                !MyString.isEmptyOrNull(oauth2ClientId);
    }

    public static class Status{
        public static final short OK = 0;
        public static final short FROZEN = 1;
        public static final short DELETED = 2;

        public static boolean isValid(short v){
            switch (v){
                case OK: case FROZEN: case DELETED:
                    return true;
                default:
                    return false;
            }
        }
    }
}
