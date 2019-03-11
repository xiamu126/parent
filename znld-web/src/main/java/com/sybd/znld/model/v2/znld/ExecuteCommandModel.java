package com.sybd.znld.model.v2.znld;

import java.io.Serializable;

public class ExecuteCommandModel implements Serializable {
    public Integer id;
    public Integer objId;
    public Integer objInstId;
    public Integer resId;
    public String value;
    public String description;
    public Short timeout;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getTimeout() {
        return timeout;
    }

    public void setTimeout(Short timeout) {
        this.timeout = timeout;
    }
}
