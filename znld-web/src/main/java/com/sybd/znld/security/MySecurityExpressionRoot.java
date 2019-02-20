package com.sybd.znld.security;

import com.sybd.znld.model.user.UserEntity;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.Arrays;

public class MySecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations  {
    private Object filterObject;
    private Object returnObject;

    public MySecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }
    public boolean isOk(){
        UserEntity user = (UserEntity)this.getPrincipal();
        String[] list =user.getAuthorities().split(",");
        boolean ret =Arrays.stream(list).anyMatch(item -> item.equalsIgnoreCase("ADMIN"));
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
