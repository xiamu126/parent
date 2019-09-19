package com.sybd.znld.onenet.controller.device.dto;

import com.sybd.znld.model.BaseApiResult;

import java.util.Map;

public class PushRegionResult extends BaseApiResult {
    public Map<Integer, Map<String, BaseApiResult>> values;
}
