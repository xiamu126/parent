package com.sybd.znld.onenet.dto;

public class CommonParams{
    public Integer deviceId;
    public String imei;
    public Integer objId;
    public Integer resId;
    public Integer objInstId;
    public Integer timeout;
    public String toUrlString(){
        return "?imei="+this.imei+"&obj_id="+this.objId+"&res_id="+this.resId+"&obj_inst_id="+this.objInstId+"&timeout="+this.timeout;
    }

    public CommonParams(Integer deviceId, String imei, Integer objId, Integer resId, Integer objInstId, Integer timeout) {
        this.deviceId = deviceId;
        this.imei = imei;
        this.objId = objId;
        this.resId = resId;
        this.objInstId = objInstId;
        this.timeout = timeout;
    }

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

    public Integer getObjId() {
        return objId;
    }

    public void setObjId(Integer objId) {
        this.objId = objId;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public Integer getObjInstId() {
        return objInstId;
    }

    public void setObjInstId(Integer objInstId) {
        this.objInstId = objInstId;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
