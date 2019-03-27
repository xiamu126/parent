package com.sybd.security.oauth2.server.core;

import com.sybd.znld.util.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

@Component("PasswordEncoder")
public class MyPasswordEncoder implements PasswordEncoder {
    private final Logger log = LoggerFactory.getLogger(MyPasswordEncoder.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    @Override
    public String encode(CharSequence rawPassword) {
        return this.encoder.encode(rawPassword);
    }
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return this.encoder.matches(rawPassword, encodedPassword);
    }
}
