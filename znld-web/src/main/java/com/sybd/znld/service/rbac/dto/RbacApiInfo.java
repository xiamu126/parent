package com.sybd.znld.service.rbac.dto;

import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public final class RbacApiInfo extends RbacInfo<RbacInfo.Api> {
    public RbacApiInfo() {
        this.rbac = new Api();
    }
    public RbacApiInfo(Builder builder){
        this();
        this.rbac.app = builder.app;
        this.rbac.detail.include.path = builder.path;
        this.rbac.detail.include.methods = builder.methods;
    }

    @Override
    public boolean isValid() {
        if(MyString.isEmptyOrNull(this.rbac.detail.include.path)) return false;
        if(this.rbac.detail.include.methods == null || this.rbac.detail.include.methods.isEmpty()) return false;
        return super.isValid();
    }

    public static final class Builder{
        private String app;
        private String path;
        private List<String> methods;

        public Builder setApp(String app){
            this.app = app;
            return this;
        }
        public Builder setPath(String path){
            this.path = path;
            return this;
        }
        public Builder setMethods(String ...methods){
            this.methods = List.of(methods);
            return this;
        }
        public RbacApiInfo build(){
            return new RbacApiInfo(this);
        }
    }
}
