package com.sybd.security.oauth2.server.config;

import com.sybd.security.oauth2.server.core.MyAuthenticationFailureHandler;
import com.sybd.security.oauth2.server.core.MyAuthenticationSuccessHandler;
import com.sybd.security.oauth2.server.core.MyLogoutSuccessHandler;
import com.sybd.security.oauth2.server.core.MyUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MyAuthenticationSuccessHandler authenticationSucessHandler;
    private final MyAuthenticationFailureHandler authenticationFailureHandler;
    private final MyLogoutSuccessHandler myLogoutSuccessHandler;
    private final MyUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(MyAuthenticationSuccessHandler authenticationSucessHandler,
                          MyAuthenticationFailureHandler authenticationFailureHandler,
                          MyLogoutSuccessHandler myLogoutSuccessHandler, MyUserDetailsService userDetailsService) {
        this.authenticationSucessHandler = authenticationSucessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.myLogoutSuccessHandler = myLogoutSuccessHandler;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/plugins/**", "/favicon.ico");
        web.ignoring().antMatchers(HttpMethod.OPTIONS);
        //web.ignoring().antMatchers("/tokens/revoke/*");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/auth","/unauth", "/success", "/error").permitAll()
                .anyRequest().authenticated();

        /*http.formLogin().usernameParameter("user").passwordParameter("password")
                .loginPage("/auth")
                .loginProcessingUrl("/auth/processing")//跳转登录页面的控制器，该地址要保证和表单提交的地址一致！
                .successHandler(authenticationSucessHandler) // 处理登录成功
                .failureHandler(authenticationFailureHandler); // 处理登录失败

        http.logout()
                .logoutUrl("/unauth")
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");*/

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.httpBasic().disable();
        http.csrf().disable().cors();//暂时禁用CSRF，否则无法提交表单

        /*ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry
                = http.authorizeRequests();
        registry.requestMatchers(CorsUtils::isPreFlightRequest).permitAll();//让Spring security放行所有preflight request*/
    }


    /**
     * 需要配置这个支持password模式
     * support password grant type
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /*@Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:8090"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","DELETE","PUT","OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }*/
}
