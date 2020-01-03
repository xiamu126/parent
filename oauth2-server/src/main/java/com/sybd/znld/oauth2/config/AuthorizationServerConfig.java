package com.sybd.znld.oauth2.config;

import com.sybd.znld.oauth2.core.MyJwtAccessTokenConverter;
import com.sybd.znld.oauth2.core.MyPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    private final AuthenticationManager authenticationManager;
    private final DataSource dataSource;
    private final UserDetailsService userDetailsService;
    private final TokenEnhancer tokenEnhancer;
    private final MyJwtAccessTokenConverter jwtAccessTokenConverter;


    @Autowired
    public AuthorizationServerConfig(AuthenticationManager authenticationManager,
                                     @Qualifier("oauthDataSource") DataSource dataSource,
                                     @Qualifier("myUserDetailsService") UserDetailsService userDetailsService,
                                     @Qualifier("myTokenEnhancer") TokenEnhancer tokenEnhancer,
                                     MyJwtAccessTokenConverter jwtAccessTokenConverter) {
        this.authenticationManager = authenticationManager;
        this.dataSource = dataSource;
        this.userDetailsService = userDetailsService;
        this.tokenEnhancer = tokenEnhancer;
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
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
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(this.tokenEnhancer, this.jwtAccessTokenConverter));
        endpoints.tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain) // 对已经存在缓存里的token不会执行这个加强
                .userDetailsService(this.userDetailsService) // 没这个，refresh_token会报错
                .authenticationManager(this.authenticationManager); // 支持 password grant type
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        var tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setSupportRefreshToken(true);
        return tokenServices;
    }

    @Bean("JwtTokenStore")
    public TokenStore tokenStore() {
        return new JwtTokenStore(this.jwtAccessTokenConverter);
    }
}
/*
http://localhost:8080/oauth/authorize?response_type=code&client_id=znld&redirect_uri=http://www.baidu.com&scope=read
http://localhost:8080/oauth/authorize?response_type=token&client_id=znld&redirect_uri=http://www.baidu.com&scope=read
* */
