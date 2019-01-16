package com.sybd.znldwebsocket.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    private final ResourceServerProperties resourceServerProperties;
    private final MyAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public ResourceServerConfig(ResourceServerProperties resourceServerProperties,
                                MyAccessDeniedHandler accessDeniedHandler) {
        this.resourceServerProperties = resourceServerProperties;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(resourceServerProperties.getResourceId());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .defaultAuthenticationEntryPointFor(
                        new Http403ForbiddenEntryPoint(),
                        new AntPathRequestMatcher("/**"));
        http.authorizeRequests().anyRequest().authenticated();
        http.cors();
    }
}

