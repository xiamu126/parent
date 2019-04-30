package com.sybd.znld.model.rbac.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class AuthPackByUser implements Serializable {
    public String userId;
    public String roleName;
    public String authName;
    public String authValue;
    public String oauth2ClientId;
}
