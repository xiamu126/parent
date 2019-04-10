package com.sybd.znld.security;

import com.sybd.znld.model.rbac.dto.RbacApiInfo;
import com.sybd.znld.model.rbac.dto.RbacHtmlInfo;
import com.sybd.znld.model.rbac.dto.RbacInfo;
import com.sybd.znld.service.rbac.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class MySecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations  {
    private Object filterObject;
    private Object returnObject;
    private UserMapper userMapper;

    public MySecurityExpressionRoot(Authentication authentication, UserMapper userMapper) {
        super(authentication);
        this.userMapper = userMapper;
    }

    public boolean isOk(String role, HttpServletRequest request){
        var userName = this.getPrincipal();
        var user = this.userMapper.selectByName(userName.toString());
        var roles = this.userMapper.selectRolesByUserId(user.id);
        if(roles.stream().noneMatch(r -> r.name.equals(role))) return false;
        var path = request.getServletPath();
        var method = request.getMethod();
        var authPack = this.userMapper.selectAuthPackByUserId(user.id);
        if(authPack == null || authPack.isEmpty()) return false;
        for(var a: authPack){
            var type = RbacInfo.getType(a.authValue);
            if(type.equals(RbacInfo.RbacType.API.getValue())){
                var tmp = RbacApiInfo.from(a.authValue);
            }else if (type.equals(RbacInfo.RbacType.HTML.getValue())){
                var tmp = RbacHtmlInfo.from(a.authValue);
            }
        }
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
