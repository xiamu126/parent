package com.sybd.znld.model.rbac;

import java.io.Serializable;

public class AuthorityModel implements Serializable {
    public String id;
    public String name;
    public String authorityGroupId;
    public String uri;
    public Short status = Status.OK;

    public static class Status{
        public static final short OK = 0;
        public static final short DELETED = 1;

        public static boolean isValid(short v){
            switch (v){
                case OK: case DELETED: return true;
                default: return false;
            }
        }
    }
}
