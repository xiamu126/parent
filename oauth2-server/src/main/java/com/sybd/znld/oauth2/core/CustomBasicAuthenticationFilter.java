package com.sybd.znld.oauth2.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Component
public class CustomBasicAuthenticationFilter extends BasicAuthenticationFilter {

    //@Autowired
    public CustomBasicAuthenticationFilter(final AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void onSuccessfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final Authentication authResult) {
        //Generate Token
        //Save the token for the logged in user
        //send token in the response
        response.setHeader("header-name" , "token");

    }

}