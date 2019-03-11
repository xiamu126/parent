package com.sybd.znld.model.v2.znld;

import java.io.Serializable;

public class RegionModel implements Serializable {
    public String id;
    public String name;
    public Short status;

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
