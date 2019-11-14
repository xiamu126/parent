package com.sybd.znld.model.rbac.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class RbacApiInfoSummary extends RbacInfo {
    public List<Detail> exclude = new ArrayList<>();
    public List<Detail> include = new ArrayList<>();

    @ToString  @Getter @Setter
    public static class Detail{
        public String path;
        public List<String> methods = new ArrayList<>();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Detail detail = (Detail) o;
            return Objects.equals(path, detail.path) && Objects.equals(methods, detail.methods);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, methods);
        }
    }
}
