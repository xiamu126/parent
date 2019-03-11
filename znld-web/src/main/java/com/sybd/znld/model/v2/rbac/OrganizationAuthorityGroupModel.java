package com.sybd.znld.model.v2.rbac;

import java.io.Serializable;

public class OrganizationAuthorityGroupModel implements Serializable {
    public String id;
    public String organizationId;
    public String authorityGroupId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getAuthorityGroupId() {
        return authorityGroupId;
    }

    public void setAuthorityGroupId(String authorityGroupId) {
        this.authorityGroupId = authorityGroupId;
    }
}
