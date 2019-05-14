package com.sybd.znld.model.rbac.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
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

    public static RbacApiInfo from(String jsonString){
        var objectMapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(RbacApiInfo.class, new MyDeserializer());
        objectMapper.registerModule(module);
        RbacApiInfo tmp = null;
        try {
            tmp = objectMapper.readValue(jsonString, RbacApiInfo.class);
        } catch (IOException ex) {
           log.error(ex.getMessage());
        }
        return tmp;
    }
    private static class MyDeserializer extends StdDeserializer<RbacApiInfo> {
        public MyDeserializer() { this(null); }
        public MyDeserializer(Class<?> vc) {
            super(vc);
        }
        @Override
        public RbacApiInfo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            var app = node.get("app").asText();
            var type = node.get("type").asText();
            var ret = new RbacApiInfo();
            ret.rbac.app = app;
            ret.rbac.type = type;
            var include = node.get("detail").get("include");
            var apiDetail = new Api.Detail();
            apiDetail.include.path = include.get("path").asText();
            apiDetail.include.methods = new ArrayList<>();
            //ArrayNode methods = (ArrayNode)include.get("methods");
            for(var item: include.get("methods")){
                TextNode tmp = (TextNode)item;
                apiDetail.include.methods.add(tmp.textValue());
            }
            ret.rbac.detail = apiDetail;
            return ret;
        }
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
