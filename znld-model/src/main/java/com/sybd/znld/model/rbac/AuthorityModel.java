package com.sybd.znld.model.rbac;

import java.io.Serializable;

public class AuthorityModel implements Serializable {
    public String id;
    public String name;
    public String authorityGroupId;
    public String url;
    public Integer type;
    public Short status;
}
