package com.sybd.znld.oauth2.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final Logger log = LoggerFactory.getLogger(MyAuthenticationFailureHandler.class);
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        log.debug(exception.getLocalizedMessage());
        redirectStrategy.sendRedirect(request, response, "/error");
    }

    @EventListener
    public void handleAbstractAuthenticationFailureEvent(AuthorizationFailureEvent event){
        //log.debug("AbstractAuthenticationEvent");
    }
}
