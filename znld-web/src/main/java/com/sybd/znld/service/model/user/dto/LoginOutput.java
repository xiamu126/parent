package com.sybd.znld.service.model.user.dto;

import java.io.Serializable;

public class LoginOutput implements Serializable {
    public String id;
    public String clientId;
    public String clientSecret;
    public Long auth2TokenExpirationTime;

    public LoginOutput(){}
    public LoginOutput(String id, String clientId, String clientSecret, Long auth2TokenExpirationTime) {
        this.id = id;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.auth2TokenExpirationTime = auth2TokenExpirationTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Long getAuth2TokenExpirationTime() {
        return auth2TokenExpirationTime;
    }

    public void setAuth2TokenExpirationTime(Long auth2TokenExpirationTime) {
        this.auth2TokenExpirationTime = auth2TokenExpirationTime;
    }
}
