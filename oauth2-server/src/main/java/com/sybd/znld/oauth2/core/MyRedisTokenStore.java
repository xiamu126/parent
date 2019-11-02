package com.sybd.znld.oauth2.core;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Component;

@Component
public class MyRedisTokenStore extends RedisTokenStore {
    @Autowired
    public MyRedisTokenStore(@Qualifier("redissonConnectionFactory") RedisConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        var token = super.getAccessToken(authentication);
        if(token == null)
            return null;
        //var seconds = token.getExpiresIn();
        //var map = this.redissonClient.getSetMultimap("userWithToken");
        //var list = super.findTokensByClientIdAndUserName("sybd_znld_test", "zhengzhou");
        //var list = super.findTokensByClientId("sybd_znld_test");
        return token;
    }
}
