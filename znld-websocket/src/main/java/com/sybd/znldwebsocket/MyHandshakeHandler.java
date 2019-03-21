package com.sybd.znldwebsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.event.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.filter.OAuth2AuthenticationFailureEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class MyHandshakeHandler extends DefaultHandshakeHandler {
    private final Logger log = LoggerFactory.getLogger(MyHandshakeHandler.class);
    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        var user = super.determineUser(request, wsHandler, attributes);
        log.debug(user == null ? "null" : user.getName());
        var uri = request.getURI();
        var arrays = uri.getPath().split("/");
        log.debug(arrays[3]);
        log.debug(uri.toString());
        return super.determineUser(request, wsHandler, attributes);
    }

    private Authentication authentication = null;

    @EventListener({AuthenticationSuccessEvent.class, InteractiveAuthenticationSuccessEvent.class})
    public void processAuthenticationSuccessEvent(AbstractAuthenticationEvent e) {
        this.authentication = e.getAuthentication();
    }

    @EventListener
    public void handleSessionConnectedEvent(SessionConnectedEvent event) {
        // Get Accessor
        var sha = StompHeaderAccessor.wrap(event.getMessage());
        log.debug("XXXXXXXXXXXXX："+sha.getMessage()+sha.getSessionId());
    }

    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event){
        log.debug("SessionConnectEvent");
    }

    @EventListener
    public void handleAuthenticationFailureCredentialsExpiredEvent(AuthenticationFailureCredentialsExpiredEvent event){
        log.debug("AuthenticationFailureCredentialsExpiredEvent");
    }

    @EventListener
    public void handleAbstractAuthenticationFailureEvent(AbstractAuthenticationFailureEvent event){
        log.debug("AbstractAuthenticationFailureEvent");
    }
}
