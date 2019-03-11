package com.sybd.znld.model.v2.rbac;

import java.io.Serializable;

public class AuthorityModel implements Serializable {
    public String id;
    public String name;
    public String authorityGroupId;
    public String url;
    public Integer type;
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

    public String getAuthorityGroupId() {
        return authorityGroupId;
    }

    public void setAuthorityGroupId(String authorityGroupId) {
        this.authorityGroupId = authorityGroupId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }
}
