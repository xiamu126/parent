package com.sybd.znld.service.model;

import java.io.Serializable;

public class OneNetConfigDeviceEntity implements Serializable {
    public Integer id;
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
    public Boolean checked;
}
