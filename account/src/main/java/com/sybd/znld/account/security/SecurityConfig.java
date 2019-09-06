package com.sybd.znld.account.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico", "/assets/**", "/static/**");
        web.ignoring().antMatchers("/api/v2/user/login/**", "/api/v2/user/register/**");
        web.ignoring().antMatchers("/webjars/**","/v2/api-docs","/swagger-resources/**","/swagger-ui.html/**", "/api/doc/**");
    }
}
