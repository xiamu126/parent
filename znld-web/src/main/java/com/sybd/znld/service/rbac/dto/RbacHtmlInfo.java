package com.sybd.znld.service.rbac.dto;

import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public final class RbacHtmlInfo extends RbacInfo<RbacInfo.Html> {
    public RbacHtmlInfo() {
        this.rbac = new RbacInfo.Html();
    }
    public RbacHtmlInfo(Builder builder){
        this();
        this.rbac.app = builder.app;
        this.rbac.detail.exclude.path = builder.path;
        this.rbac.detail.exclude.selectors = builder.selectors;
    }
    @Override
    public boolean isValid() {
        if(MyString.isEmptyOrNull(this.rbac.detail.exclude.path)) return false;
        if(this.rbac.detail.exclude.selectors == null || this.rbac.detail.exclude.selectors.isEmpty()) return false;
        return super.isValid();
    }

    public static final class Builder{
        private String app;
        private String path;
        private List<String> selectors;

        public Builder setApp(String app){
            this.app = app;
            return this;
        }
        public Builder setPath(String path){
            this.path = path;
            return this;
        }
        public Builder setSelectors(String ...selectors){
            this.selectors = List.of(selectors);
            return this;
        }

        public RbacHtmlInfo build(){
            return new RbacHtmlInfo(this);
        }
    }
}
