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
import java.util.Objects;

@Slf4j
public final class RbacWebInfo extends RbacWebInfoSummary{
    public String app;
    public String type;

    public RbacWebInfo(String app) {
        this.app = app;
        this.type = Type.WEB.getValue();
    }
    public List<Detail> exclude = new ArrayList<>();
    public List<Detail> include = new ArrayList<>();

    public static class Detail{
        public String path;
        public List<String> selectors;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            var detail = (Detail) o;
            return Objects.equals(path, detail.path) && Objects.equals(selectors, detail.selectors);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, selectors);
        }
    }
}
