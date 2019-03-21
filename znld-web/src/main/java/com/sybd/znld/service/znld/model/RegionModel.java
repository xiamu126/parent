package com.sybd.znld.service.znld.model;

import java.io.Serializable;

public class RegionModel implements Serializable {
    public String id;
    public String name;
    public Short status = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }
}
