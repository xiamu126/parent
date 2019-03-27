package com.sybd.znld.model.rbac;

import java.io.Serializable;

public class RoleModel implements Serializable {
    public String id;
    public String name;
    public Short type;
    public Short status = Status.OK;

    public static class Type{
        public static final short ADMIN = 0;
        public static final short USER = 1;
        public static final short ANONYMOUS = 2;

        public static boolean isValid(short v){
            switch (v) {
                case ADMIN: case USER: case ANONYMOUS: return true;
                default: return false;
            }
        }
    }

    public static class Status{
        public static final short OK = 0;
        public static final short FROZEN = 1;
        public static final short DELETED = 2;

        public static boolean isValid(short v){
            switch (v){
                case OK: case FROZEN: case DELETED: return true;
                default: return false;
            }
        }
    }
}
