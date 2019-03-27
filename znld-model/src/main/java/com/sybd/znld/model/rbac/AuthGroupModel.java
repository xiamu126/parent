package com.sybd.znld.model.rbac;

import java.io.Serializable;

public class AuthGroupModel implements Serializable {
    public String id;
    public String name;
    public String parentId;
    public Integer position;
    public Short status = Status.OK;

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
