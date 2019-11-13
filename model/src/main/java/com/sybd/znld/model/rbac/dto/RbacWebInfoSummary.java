package com.sybd.znld.model.rbac.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class RbacWebInfoSummary extends RbacInfo {
    public List<RbacWebInfo.Detail> exclude = new ArrayList<>();
    public List<RbacWebInfo.Detail> include = new ArrayList<>();

    public static class Detail{
        public String path;
        public List<String> selectors;
    }
}
