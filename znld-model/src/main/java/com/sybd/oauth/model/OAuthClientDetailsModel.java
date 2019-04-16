package com.sybd.oauth.model;

import java.io.Serializable;

public class OAuthClientDetailsModel implements Serializable {
    public String clientId;
    public String resourceIds;
    public String clientSecret;
    public String scope;
    public String authorizedGrantTypes;
    public String webServerRedirectUri;
    public String authorities;
    public Integer accessTokenValidity;
    public Integer refreshTokenValidity;
    public String additionalInformation;
    public String autoapprove;
}
