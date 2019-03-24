package com.sybd.znld.model.rbac;

import java.io.Serializable;

public class AuthorityGroupModel implements Serializable {
    public String id;
    public String name;
    public String parentId;
    public Integer position;
    public Short status;
}
