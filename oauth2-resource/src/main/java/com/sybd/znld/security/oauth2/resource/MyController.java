package com.sybd.znld.security.oauth2.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class MyController {
    private final Logger log = LoggerFactory.getLogger(MyController.class);

    //@PreAuthorize("hasAnyAuthority('ADMIN', 'well')")
    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/user")
    public Authentication getUser(Authentication authentication, Principal principal) {
        log.info("resource: user {}", authentication);
        log.info(principal.toString());
        return authentication;
    }
}
