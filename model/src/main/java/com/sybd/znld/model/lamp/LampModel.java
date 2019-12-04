package com.sybd.znld.model.lamp;

import com.sybd.znld.model.IValidForDBInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter @ToString
public class LampModel implements Serializable, IValidForDBInsert {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String apiKey;
    public Integer deviceId;
    public String imei;
    public String deviceName;
    public String longitude = "";
    public String latitude = "";
    public Status status = Status.LAMP_OK;
    public Float xAngle = 0.0f;
    public Float yAngle = 0.0f;
    public Integer linkTo = 0;

    public boolean isLongitudeLatitudeAssigned(){
        return !MyString.isEmptyOrNull(this.longitude) && !MyString.isEmptyOrNull(this.latitude);
    }

    @Override
    public boolean isValidForInsert() {
        // 经纬度要么同时为未设置状态，要么都设置了，不能设置了一个而另一个没设置
        if(MyString.isEmptyOrNull(this.longitude) && !MyString.isEmptyOrNull(this.latitude)){
            return false;
        }
        if(!MyString.isEmptyOrNull(this.longitude) && MyString.isEmptyOrNull(this.latitude)){
            return false;
        }
        if(!MyNumber.isPositive(this.deviceId)) {
            return false;
        }
        if(!MyString.isUuid(this.id)) {
            return false;
        }
        return !MyString.isAnyEmptyOrNull(this.apiKey, this.imei, this.deviceName);
    }
}
