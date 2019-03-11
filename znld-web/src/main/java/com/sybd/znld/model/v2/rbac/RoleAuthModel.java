package com.sybd.znld.model.v2.rbac;

import java.io.Serializable;

public class RoleAuthModel implements Serializable {
    public String id;
    public String roleId;
    public String authId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }
}
