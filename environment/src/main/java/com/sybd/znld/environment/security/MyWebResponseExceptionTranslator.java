package com.sybd.znld.environment.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

public class MyWebResponseExceptionTranslator implements WebResponseExceptionTranslator {
    @Override
    public ResponseEntity translate(Exception e) throws Exception {
        var response = new ResponseEntity<String>("null", HttpStatus.FORBIDDEN);
        return response;
    }
}
