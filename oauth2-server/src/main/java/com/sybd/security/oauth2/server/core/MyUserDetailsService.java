package com.sybd.security.oauth2.server.core;

import com.sybd.security.oauth2.server.db.DbSource;
import com.sybd.security.oauth2.server.mapper.UserMapper;
import com.sybd.security.oauth2.server.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MyUserDetailsService implements UserDetailsService {
    private final UserMapper userMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public MyUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @DbSource("znld")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userMapper.selectByName(username);
        if(user == null) throw new UsernameNotFoundException(username);
        String tmp = user.getAuthorities();
        String[] auth = tmp.split(",");
        return User.builder().username(username).password(user.getPassword()).authorities(auth).build();
    }
}
