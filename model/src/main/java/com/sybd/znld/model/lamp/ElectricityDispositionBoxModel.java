package com.sybd.znld.model.lamp;

import com.sybd.znld.model.Status;

import java.util.UUID;

public class ElectricityDispositionBoxModel {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String apiKey;
    public String deviceId;
    public String imei;
    public String name;
    public Double rawLongitude;
    public Double rawLatitude;
    public Double lng;
    public Double lat;
    public Status status = Status.OK;
}
