package com.sybd.znld.oauth2.config;

import com.sybd.znld.oauth2.core.MyRedisTokenStore;
import com.sybd.znld.oauth2.core.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    private final AuthenticationManager authenticationManager;
    private final DataSource dataSource;
    private final MyUserDetailsService userDetailsService;
    private final MyRedisTokenStore tokenStore;

    @Autowired
    public AuthorizationServerConfig(AuthenticationManager authenticationManager,
                                     @Qualifier("oauthDataSource") DataSource dataSource,
                                     MyUserDetailsService userDetailsService,
                                     MyRedisTokenStore tokenStore) {
        this.authenticationManager = authenticationManager;
        this.dataSource = dataSource;
        this.userDetailsService = userDetailsService;
        this.tokenStore = tokenStore;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security){
        // 配置token获取和验证时的策略
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();//enable client to get the authenticated when using the /oauth/token to get a access token
        // there is a 401 authentication is required if it doesn't allow form authentication for clients when access /oauth/token
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore)
                .userDetailsService(this.userDetailsService) // 没这个，refresh_token会报错
                .authenticationManager(authenticationManager); // 支持 password grant type
    }

/*    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }*/
}
/*
http://localhost:8080/oauth/authorize?response_type=code&client_id=znld&redirect_uri=http://www.baidu.com&scope=read
http://localhost:8080/oauth/authorize?response_type=token&client_id=znld&redirect_uri=http://www.baidu.com&scope=read
* */
