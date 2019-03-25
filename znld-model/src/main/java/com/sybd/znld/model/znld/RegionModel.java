package com.sybd.znld.model.znld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class RegionModel implements Serializable {
    public String id;
    public String name;
    public Short status = 0;

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
