package com.sybd.znld.service.ministar.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TwinkleBeautyGroup implements Serializable {
    public static class Status{
        public static final short UPLOADED = 0;
        public static final short RUNNING = 1;
        public static final short STOPPED = 2;
        public static final short STOPPED_MANUALLY = 3;
        public static final short NETWORK_ERROR = 4;
        public static final short UNKNOWN_ERROR = 5;
        public static boolean isValid(short v){
            switch (v){
                case UPLOADED:
                case RUNNING:
                case STOPPED:
                case STOPPED_MANUALLY:
                case NETWORK_ERROR:
                case UNKNOWN_ERROR:
                    return true;
                default:
                    return false;
            }
        }
    }
    String id;
    LocalDateTime beginTime;
    LocalDateTime endTime;
    Short status;
    String regionId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
}
