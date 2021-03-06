package com.sybd.znld.web.controller.device.dto;

import com.sybd.znld.model.BaseApiResult;

import java.util.ArrayList;
import java.util.List;

public class PullRegionResult extends BaseApiResult {
    public List<Item> values;

    public static class Item{
        public Integer deviceId;
        public String deviceName;
        public List<ResourceStatus> status = new ArrayList<>();

        public static class ResourceStatus{
            public String name;
            public Integer code;
            public String msg;
            public Object value;
        }
    }
}
