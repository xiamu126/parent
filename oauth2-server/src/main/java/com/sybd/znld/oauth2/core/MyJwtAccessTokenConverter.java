package com.sybd.znld.oauth2.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

@Component("myJwtAccessTokenConverter")
public class MyJwtAccessTokenConverter extends JwtAccessTokenConverter {
    @Autowired
    public MyJwtAccessTokenConverter(MyAccessTokenConverter accessTokenConverter) {
        // final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("mytest.jks"), "mypass".toCharArray());
        // this.setKeyPair(keyStoreKeyFactory.getKeyPair("mytest"));
        this.setSigningKey("sybd");
        this.setAccessTokenConverter(accessTokenConverter);
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        var result = new DefaultOAuth2AccessToken(accessToken);
        var refresh = (ExpiringOAuth2RefreshToken)(result.getRefreshToken());
        var expiration = refresh.getExpiration();
        var token = super.enhance(accessToken, authentication);
        var refreshToken = token.getRefreshToken();
        return token;
    }
}
