package com.sybd.znld.service.model;

import java.io.Serializable;

public class ExecuteCommandEntity implements Serializable {
    public Integer id;
    public Integer objId;
    public Integer objInstId;
    public Integer resId;
    public String value;
    public String description;
    public Integer timeout;

    //数据库中使用的是int unsigned，导致mybatis返回的是Long类型
    public ExecuteCommandEntity(Long id, Long objId, Long objInstId, Long resId, String value, String description, Integer timeout) {
        this.id = id.intValue();
        this.objId = objId.intValue();
        this.objInstId = objInstId.intValue();
        this.resId = resId.intValue();
        this.value = value;
        this.description = description;
        this.timeout = timeout;
    }
    public ExecuteCommandEntity(Integer id, Integer objId, Integer objInstId, Integer resId, String value, String description, Integer timeout) {
        this.id = id;
        this.objId = objId;
        this.objInstId = objInstId;
        this.resId = resId;
        this.value = value;
        this.description = description;
        this.timeout = timeout;
    }

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

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
