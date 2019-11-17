package com.sybd.znld.model.lamp;

import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;
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
    public Integer status = Status.OK.getValue();
    public Float xAngle = 0.0f;
    public Float yAngle = 0.0f;
    public Integer linkTo = 0;

    public boolean isValidBeforeInsert(){
        // 经纬度要么同时为未设置状态，要么都设置了，不能设置了一个而另一个没设置
        if(MyString.isEmptyOrNull(this.longitude) && !MyString.isEmptyOrNull(this.latitude)){
            return false;
        }
        if(!MyString.isEmptyOrNull(this.longitude) && MyString.isEmptyOrNull(this.latitude)){
            return false;
        }
        return Status.getStatus(this.status) != null && !MyString.isAnyEmptyOrNull(this.apiKey, this.deviceId.toString(), this.imei, this.deviceName);
    }
    public boolean isLongitudeLatitudeAssigned(){
        return !MyString.isEmptyOrNull(this.longitude) && !MyString.isEmptyOrNull(this.latitude);
    }
}
