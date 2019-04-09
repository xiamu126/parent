package com.sybd.znld.security;

import com.sybd.znld.service.rbac.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

@Slf4j
public class MySecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations  {
    private Object filterObject;
    private Object returnObject;
    private UserMapper userMapper;

    public MySecurityExpressionRoot(Authentication authentication, UserMapper userMapper) {
        super(authentication);
        this.userMapper = userMapper;
    }

    public boolean isOk(){
        var userName = this.getPrincipal();
        var user = this.userMapper.selectByName(userName.toString());
        var tmp = this.userMapper.selectAuthPackByUserId(user.id);
        log.debug(tmp.toString());
        var auth = this.getAuthentication().getAuthorities();
        var ret = auth.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        return ret;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    public void setFilterObject(Object obj) {
        this.filterObject = obj;
    }

    @Override
    public void setReturnObject(Object obj) {
        this.returnObject = obj;
    }
}
