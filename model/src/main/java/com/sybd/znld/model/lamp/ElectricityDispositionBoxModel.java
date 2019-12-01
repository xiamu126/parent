package com.sybd.znld.model.lamp;

import com.sybd.znld.model.IValidForDBInsert;
import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;

import java.util.UUID;

public class ElectricityDispositionBoxModel implements IValidForDBInsert {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String apiKey;
    public Integer deviceId;
    public String imei;
    public String name;
    public Double rawLongitude;
    public Double rawLatitude;
    public Double lng;
    public Double lat;
    public Status status = Status.OK;

    @Override
    public boolean isValidForInsert() {
        if(MyString.isEmptyOrNull(apiKey)) return false;
        if(!MyNumber.isPositive(deviceId)) return false;
        if(MyString.isEmptyOrNull(imei)) return false;
        return !MyString.isEmptyOrNull(name);
    }
}