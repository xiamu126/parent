package com.sybd.znld.web.controller.device.dto;

import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.BaseApiResult;

import java.util.List;
import java.util.Map;

public class PullRegionResult extends BaseApiResult {
    public Map<Integer, Map<String, ApiResult>> values;
}
