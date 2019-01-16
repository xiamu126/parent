package com.sybd.znld.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class LoginOutput implements Serializable {
    private String id;
    private String clientId;
    private String clientSecret;
    private Long auth2TokenExpirationTime;
}
