package com.sybd.znld.model.rbac.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sybd.znld.model.IValid;
import com.sybd.znld.znld.util.MyString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Slf4j
public abstract class RbacInfo<T extends RbacInfo.Base> implements IValid, Serializable {
    public T rbac;

    public String getJsonString(){
        var objectMapper = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(this.rbac);
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage());
        }
        return jsonStr;
    }
    public static String getType(String json){
        var objectMapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(Base.class, new RbacTypeDeserializer());
        objectMapper.registerModule(module);
        try {
            var tmp = objectMapper.readValue(json, Base.class);
            return tmp == null ? "" : tmp.type;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return "";
    }

    @Override
    public boolean isValid() {
        if(MyString.isEmptyOrNull(this.rbac.app)) return false;
        if(MyString.isEmptyOrNull(this.rbac.type)) return false;
        return true;
    }

    private static class RbacTypeDeserializer extends StdDeserializer<Base>{
        public RbacTypeDeserializer() {
            this(null);
        }
        public RbacTypeDeserializer(Class<?> vc) {
            super(vc);
        }
        @Override
        public Base deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            var type = node.get("type").asText();
            var app = node.get("app").asText();
            return new Base(app, type);
        }
    }
    @NoArgsConstructor @AllArgsConstructor
    public static class Base {
        public String app;
        public String type;
    }
    public static class Html extends Base {
        public Html() { this.type = RbacType.HTML.getValue(); }
        public Detail detail = new Detail();
        public static class Detail{
            public Exclude exclude = new Exclude();
            public static class Exclude{
                public String path;
                public List<String> selectors;
            }
        }
    }
    public static class Api extends Base {
        public Api() { this.type = RbacType.API.getValue(); }
        public Detail detail = new Detail();
        public static class Detail{
            public Include include = new Include();
            public static class Include{
                public List<String> methods;
                public String path;
            }
        }
    }
    public enum RbacType{
        HTML("html"),API("api");
        RbacType(String value){
            this.value = value;
        }
        private String value;
        public String getValue(){
            return this.value;
        }
    }
}
