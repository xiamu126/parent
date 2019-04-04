package com.sybd.znld.model.znld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class LampModel implements Serializable {
    public String id;
    public String apiKey;
    public String deviceId;
    public String imei;
    public String deviceName;
    public String longitude;
    public String latitude;
    public Short status = Status.OK;

    public static class Status{
        public static final short OK = 0;
        public static final short ERROR = 1;
        public static final short DEAD = 2;

        public static boolean isValid(short v){
            switch (v){
                case OK: case ERROR: case DEAD:
                    return true;
                default:
                    return false;
            }
        }
    }
}
