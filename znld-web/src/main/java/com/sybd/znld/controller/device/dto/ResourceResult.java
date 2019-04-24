package com.sybd.znld.controller.device.dto;

import com.sybd.znld.core.BaseApiResult;
import io.swagger.annotations.ApiModel;
import lombok.NoArgsConstructor;

@ApiModel(value = "获取可用的环境监测资源代码")
@NoArgsConstructor
public class ResourceResult extends BaseApiResult {
    public String objId;
    public String objInstId;
    public String resId;
    public String name;
}
