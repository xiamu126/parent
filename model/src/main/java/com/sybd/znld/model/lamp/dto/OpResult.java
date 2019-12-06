package com.sybd.znld.model.lamp.dto;

import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.onenet.dto.BaseResult;

import java.util.Map;

public class OpResult extends BaseApiResult {
    public Map<String, BaseResult> values;
}
