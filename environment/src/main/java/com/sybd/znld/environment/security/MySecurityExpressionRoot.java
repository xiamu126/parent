package com.sybd.znld.environment.security;

import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.model.rbac.dto.RbacApiInfo;
import com.sybd.znld.model.rbac.dto.RbacInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.util.AntPathMatcher;

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

    public boolean isRequestAllowed(HttpServletRequest request){
        var userName = this.getPrincipal();
        var user = this.userMapper.selectByName(userName.toString());
        var path = request.getRequestURI();
        var method = request.getMethod();
        var authPack = this.userMapper.selectAuthPackByUserId(user.id);
        if(authPack == null || authPack.isEmpty()) return false;
        var antPathMatcher = new AntPathMatcher();
        for(var a: authPack){
            var type = RbacInfo.getType(a.authValue);
            if(type.equals(RbacInfo.RbacType.API.getValue())){
                var rbacApiInfo = RbacApiInfo.from(a.authValue);
                if(antPathMatcher.match(rbacApiInfo.rbac.detail.include.path, path)){ // 请求的路径是允许的
                    if(rbacApiInfo.rbac.detail.include.methods.stream().anyMatch(m -> m.equals("*"))){ // 支持任意的请求方法
                        return true;
                    }
                    if(rbacApiInfo.rbac.detail.include.methods.stream().anyMatch(m -> m.equals(method))){ // 请求的方法是允许的
                        return true;
                    }
                }
            }else if (type.equals(RbacInfo.RbacType.HTML.getValue())){ // 这部分内容交给前端处理，不做处理
                //var tmp = RbacHtmlInfo.from(a.authValue);
            }
        }
       /* var auth = this.getAuthentication().getAuthorities();
        var ret = auth.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));*/
        return false;
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
