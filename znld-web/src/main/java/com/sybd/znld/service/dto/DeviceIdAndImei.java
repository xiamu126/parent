package com.sybd.znld.service.dto;

import java.io.Serializable;

public class DeviceIdAndImei implements Serializable {
    public Integer deviceId;
    public String imei;

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
