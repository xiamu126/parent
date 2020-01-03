package com.sybd.znld.oauth2.core;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("myAccessTokenConverter")
public class MyAccessTokenConverter extends DefaultAccessTokenConverter {
    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        var auth = super.extractAuthentication(map);
        auth.setDetails(map);
        return auth;
    }
}
