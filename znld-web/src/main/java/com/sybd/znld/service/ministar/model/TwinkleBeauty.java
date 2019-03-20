package com.sybd.znld.service.ministar.model;

import java.io.Serializable;

public class TwinkleBeauty implements Serializable {
    String id;
    String color;
    Short type;
    Short rate;
    String userId;
    String twinkleBeautyGroupId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Short getRate() {
        return rate;
    }

    public void setRate(Short rate) {
        this.rate = rate;
    }

    public String getTwinkleBeautyGroupId() {
        return twinkleBeautyGroupId;
    }

    public void setTwinkleBeautyGroupId(String twinkleBeautyGroupId) {
        this.twinkleBeautyGroupId = twinkleBeautyGroupId;
    }
}
