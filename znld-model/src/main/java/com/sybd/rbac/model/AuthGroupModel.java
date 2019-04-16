package com.sybd.rbac.model;

import java.io.Serializable;

public class AuthGroupModel implements Serializable {
    public String id;
    public String name;
    public String parentId;
    public Integer position;
    public String organizationId;
    public Short status = Status.OK;

    public static class Status{
        public static final short OK = 0;
        public static final short DELETED = 1;

        public static boolean isValid(short v){
            switch (v){
                case OK: case DELETED:
                    return true;
                default:
                    return false;
            }
        }
    }
}
