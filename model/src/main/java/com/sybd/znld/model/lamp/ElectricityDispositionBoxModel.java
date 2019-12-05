package com.sybd.znld.model.lamp;

import com.sybd.znld.model.DeviceStatus;
import com.sybd.znld.model.IValidForDbInsert;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;

import java.util.UUID;

public class ElectricityDispositionBoxModel implements IValidForDbInsert {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String apiKey;
    public Integer deviceId;
    public String imei;
    public String name;
    public Double rawLongitude;
    public Double rawLatitude;
    public Double lng;
    public Double lat;
    public DeviceStatus status = DeviceStatus.OK;

    @Override
    public boolean isValidForInsert() {
        if(MyString.isEmptyOrNull(apiKey)) return false;
        if(!MyNumber.isPositive(deviceId)) return false;
        if(MyString.isEmptyOrNull(imei)) return false;
        return !MyString.isEmptyOrNull(name);
    }
}
