package com.sybd.znld.service.rbac.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class AuthPack implements Serializable {
    public String userId;
    public String authName;
    public String authValue;
    public String oauth2ClientId;
}
