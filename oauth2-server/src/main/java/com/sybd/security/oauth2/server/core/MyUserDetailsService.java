package com.sybd.security.oauth2.server.core;

import com.sybd.security.oauth2.server.db.DbSource;
import com.sybd.security.oauth2.server.mapper.UserMapper;
import com.sybd.security.oauth2.server.service.AuthorityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MyUserDetailsService implements UserDetailsService {
    private final Logger log = LoggerFactory.getLogger(MyUserDetailsService.class);
    private final UserMapper userMapper;
    private final AuthorityService authorityService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public MyUserDetailsService(UserMapper userMapper, AuthorityService authorityService) {
        this.userMapper = userMapper;
        this.authorityService = authorityService;
    }

    @DbSource("rbac")
    @Override
    public UserDetails loadUserByUsername(String username) throws AuthenticationException {
        var user = userMapper.selectByName(username);
        if(user == null) throw new UsernameNotFoundException(username);
        var roles = this.authorityService.getRolesByUserId(user.id);
        var roleNames = roles.stream().map(r -> r.name).toArray(String[]::new);
        if(roleNames.length == 0) throw new BadCredentialsException(username);
        return User.builder().username(username).password(user.password).authorities(roleNames).build();
    }
}
