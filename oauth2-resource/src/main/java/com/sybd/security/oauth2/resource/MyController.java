package com.sybd.security.oauth2.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
public class MyController {
    //@PreAuthorize("hasAnyAuthority('ADMIN', 'well')")
    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/user")
    public Authentication getUser(Authentication authentication, Principal principal) {
        log.info("resource: user {}", authentication);
        log.info(principal.toString());
        return authentication;
    }
}
