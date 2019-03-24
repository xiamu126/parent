package com.sybd.znld.service.rbac.dto;

import java.io.Serializable;

public class LoginOutput implements Serializable {
    public String id;
    public String clientId;
    public String clientSecret;
    public Long auth2TokenExpirationTime;
}
