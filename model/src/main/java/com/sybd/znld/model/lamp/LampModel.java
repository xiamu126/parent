package com.sybd.znld.model.lamp;

import com.sybd.znld.znld.util.MyString;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class LampModel implements Serializable {
    public String id;
    public String apiKey;
    public Integer deviceId;
    public String imei;
    public String deviceName;
    public String longitude = "";
    public String latitude = "";
    public Short status = Status.OK;

    public boolean isValidBeforeInsert(){
        // 经纬度要么同时为未设置状态，要么都设置了，不能设置了一个而另一个没设置
        if(MyString.isEmptyOrNull(this.longitude) && !MyString.isEmptyOrNull(this.latitude)){
            return false;
        }
        if(!MyString.isEmptyOrNull(this.longitude) && MyString.isEmptyOrNull(this.latitude)){
            return false;
        }
        return Status.isValid(this.status) && !MyString.isAnyEmptyOrNull(this.apiKey, this.deviceId.toString(), this.imei, this.deviceName);
    }
    public boolean isLongitudeLatitudeAssigned(){
        return !MyString.isEmptyOrNull(this.longitude) && !MyString.isEmptyOrNull(this.latitude);
    }

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
