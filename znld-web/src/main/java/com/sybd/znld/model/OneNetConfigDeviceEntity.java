package com.sybd.znld.model;

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

    public OneNetConfigDeviceEntity(Integer id, String apiKey, String deviceId, String imei, Integer objId, Integer objInstId, Integer resId, String name, String description, Integer timeout, String longitude, String latitude, String deviceName, Boolean checked) {
        this.id = id;
        this.apiKey = apiKey;
        this.deviceId = deviceId;
        this.imei = imei;
        this.objId = objId;
        this.objInstId = objInstId;
        this.resId = resId;
        this.name = name;
        this.description = description;
        this.timeout = timeout;
        this.longitude = longitude;
        this.latitude = latitude;
        this.deviceName = deviceName;
        this.checked = checked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Integer getObjId() {
        return objId;
    }

    public void setObjId(Integer objId) {
        this.objId = objId;
    }

    public Integer getObjInstId() {
        return objInstId;
    }

    public void setObjInstId(Integer objInstId) {
        this.objInstId = objInstId;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
