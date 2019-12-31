package com.sybd.znld.account.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
        web.ignoring().antMatchers(HttpMethod.GET, "/api/v2/user/login/captcha"); // 获取验证码
        web.ignoring().antMatchers(HttpMethod.POST, "/api/v2/user/login"); // 登入
        web.ignoring().antMatchers(HttpMethod.POST, "/api/v2/user/register"); // 注册
        web.ignoring().antMatchers("/webjars/**","/v2/api-docs","/swagger-resources/**","/swagger-ui.html/**", "/api/doc/**");
    }
}
