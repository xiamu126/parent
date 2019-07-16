package com.sybd.znld.web.controller.device.dto;

import com.sybd.znld.model.BaseApiResult;

import java.util.ArrayList;
import java.util.List;

public class AngleRegionResult extends BaseApiResult {
    public List<Item> values = new ArrayList<>();
    public static class Item{
        public Integer deviceId;
        public String deviceName;
        public String value;
        public Integer code;
        public String msg;
    }
}
