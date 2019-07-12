package com.sybd.znld.web.controller.device.dto;

import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.BaseApiResult;

import java.util.List;
import java.util.Map;

public class PullResult extends BaseApiResult {
    public Map<String, ApiResult> values;
}
