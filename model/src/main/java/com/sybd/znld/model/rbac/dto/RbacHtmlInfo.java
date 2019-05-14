package com.sybd.znld.model.rbac.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
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

    public static RbacHtmlInfo from(String jsonString){
        var objectMapper = new ObjectMapper();
        RbacHtmlInfo tmp = null;
        try {
            tmp = objectMapper.readValue(jsonString, RbacHtmlInfo.class);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return tmp;
    }
    private static class MyDeserializer extends StdDeserializer<RbacHtmlInfo> {
        public MyDeserializer() { this(null); }
        public MyDeserializer(Class<?> vc) {
            super(vc);
        }
        @Override
        public RbacHtmlInfo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            var app = node.get("app").asText();
            var type = node.get("type").asText();
            var ret = new RbacHtmlInfo();
            ret.rbac.app = app;
            ret.rbac.type = type;
            var exclude = node.get("detail").get("exclude");
            var htmlDetail = new Html.Detail();
            htmlDetail.exclude.path = exclude.get("path").asText();
            htmlDetail.exclude.selectors = new ArrayList<>();
            //ArrayNode methods = (ArrayNode)exclude.get("selectors");
            for(var item: exclude.get("selectors")){
                TextNode tmp = (TextNode)item;
                htmlDetail.exclude.selectors.add(tmp.textValue());
            }
            ret.rbac.detail = htmlDetail;
            return ret;
        }
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
