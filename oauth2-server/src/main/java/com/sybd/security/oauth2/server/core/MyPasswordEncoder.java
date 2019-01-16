package com.sybd.security.oauth2.server.core;

import com.whatever.util.MD5;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

@Slf4j
@Component("PasswordEncoder")
public class MyPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        try {
            var tmp = MD5.encrypt(MD5.encrypt(rawPassword.toString()).toLowerCase());
            return tmp;
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
