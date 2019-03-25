package com.sybd.security.oauth2.server.core;

import com.sybd.znld.util.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

@Component("PasswordEncoder")
public class MyPasswordEncoder implements PasswordEncoder {

    private final Logger log = LoggerFactory.getLogger(MyPasswordEncoder.class);

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            return MD5.encrypt(MD5.encrypt(rawPassword.toString()).toLowerCase());
        } catch (NoSuchAlgorithmException ex) {
            log.error(ex.getMessage());
        }
        return null;
    }
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            //var tmp = MD5.encrypt(MD5.encrypt(MD5.encrypt(rawPassword.toString()).toLowerCase()).toLowerCase(), 2);
            var tmp = MD5.encrypt(rawPassword.toString(), 2);
            return tmp.equals(encodedPassword);
        } catch (NoSuchAlgorithmException ex) {
            log.error(ex.getMessage());
        }
        return false;
    }
}
