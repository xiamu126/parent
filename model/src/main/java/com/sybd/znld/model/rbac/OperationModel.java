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
    public Integer type;
    public Integer status = Status.OPERATION_ALLOW.getValue();

    @Override
    public boolean isValidForInsert(){
        if(!MyString.isUuid(id)) return false;
        if(MyString.isEmptyOrNull(name)) return false;
        if(MyString.isEmptyOrNull(path)) return false;
        if(MyString.isEmptyOrNull(resource)) return false;
        if(!MyString.isUuid(organizationId)) return false;
        if(MyString.isEmptyOrNull(app)) return false;
        Type typeTmp = Type.getType(type);
        if(typeTmp == null) return false; // 不用再进一步判断isValid的值，因为type值如果不是API(0)或 WEB(1)，返回空
        Status tmp = Status.getStatus(status);
        if(tmp == null) return false;
        switch (tmp){ // 需要进一步判断，因为Operation不会覆盖Status的所有值
            case OPERATION_ALLOW: case OPERATION_DENY: return true;
            default: return false;
        }
    }

    public enum Type{
        API(0), WEB(1),;
        Type(int v){
            this.value = v;
        }
        private int value;
        public int getValue(){
            return this.value;
        }
        public static Type getType(int v){
            switch (v){
                case 0: return API;
                case 1: return WEB;
            }
            return null;
        }
    }
}
