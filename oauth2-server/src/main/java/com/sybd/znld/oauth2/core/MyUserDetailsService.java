package com.sybd.znld.oauth2.core;

import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.service.rbac.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MyUserDetailsService implements UserDetailsService {
    private final UserMapper userMapper;
    private final AuthorityService authorityService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public MyUserDetailsService(UserMapper userMapper, AuthorityService authorityService) {
        this.userMapper = userMapper;
        this.authorityService = authorityService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws AuthenticationException {
        var user = userMapper.selectByName(username);
        if(user == null) throw new UsernameNotFoundException(username);
        var roles = this.authorityService.getRolesByUserId(user.id);
        if(roles == null || roles.isEmpty() || roles.stream().anyMatch(Objects::isNull)){
            throw new BadCredentialsException(username+"未关联任何角色");
        }
        var roleNames = roles.stream().map(r -> r.name).toArray(String[]::new);
        if(roleNames.length == 0) throw new BadCredentialsException(username);
        return User.builder().username(username).password(user.password).authorities(roleNames).build();
    }
}
