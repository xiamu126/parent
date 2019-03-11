package com.sybd.znld.model.v2.rbac;

import java.io.Serializable;

public class OrganizationModel implements Serializable {
    public String id;
    public String name;
    public String parentId;
    public Integer position;
    public Short status;
    public String oauth2ClientId;

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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getOauth2ClientId() {
        return oauth2ClientId;
    }

    public void setOauth2ClientId(String oauth2ClientId) {
        this.oauth2ClientId = oauth2ClientId;
    }
}
