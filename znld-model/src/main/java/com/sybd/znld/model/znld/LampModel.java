package com.sybd.znld.model.znld;

import java.io.Serializable;

public class LampModel implements Serializable {
    public String id;
    public String apiKey;
    public String deviceId;
    public String imei;
    public Integer objId;
    public Integer objInstId;
    public Integer resId;
    public String name;
    public String description;
    public Integer timeout;
    public String longitude;
    public String latitude;
    public String deviceName;
    public Short status;
    public String organizationId;

    public static class Status{
        public static final short CHECKED = 0;
        public static final short UNCHECKED = 1;
        public static final short DELETED = 2;

        public static boolean isValid(short v){
            switch (v){
                case CHECKED: case UNCHECKED: case DELETED:
                    return true;
                default:
                    return false;
            }
        }
    }
}
