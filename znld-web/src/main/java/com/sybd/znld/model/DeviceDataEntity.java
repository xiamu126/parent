package com.sybd.znld.model;

import java.io.Serializable;

public class DeviceDataEntity implements Serializable {
    public String id;
    public String at;
    public String theId;
    public String theValue;

    public DeviceDataEntity(String id, String at, String theId, String theValue) {
        this.id = id;
        this.at = at;
        this.theId = theId;
        this.theValue = theValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getTheId() {
        return theId;
    }

    public void setTheId(String theId) {
        this.theId = theId;
    }

    public String getTheValue() {
        return theValue;
    }

    public void setTheValue(String theValue) {
        this.theValue = theValue;
    }
}
