package com.sybd.znld.security;

import com.sybd.znld.model.rbac.UserModel;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

public class MySecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations  {
    private Object filterObject;
    private Object returnObject;

    public MySecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public boolean isOk(){
        var user = this.getPrincipal();
        var tmp = this.getThis();
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
