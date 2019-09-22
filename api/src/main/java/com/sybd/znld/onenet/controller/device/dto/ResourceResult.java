package com.sybd.znld.onenet.controller.device.dto;

import com.sybd.znld.model.BaseApiResult;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(value = "获取可用的环境监测资源代码")
@NoArgsConstructor
@Getter @Setter
public class ResourceResult extends BaseApiResult {
    public String objId;
    public String objInstId;
    public String resId;
    public String name;
}
