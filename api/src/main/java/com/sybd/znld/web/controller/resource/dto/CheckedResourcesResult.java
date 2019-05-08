package com.sybd.znld.web.controller.resource.dto;

import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.dto.CheckedResource;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@ApiModel(value = "获取选中资源的返回值")
@Getter @Setter
@NoArgsConstructor
public class CheckedResourcesResult extends BaseApiResult {
    public List<CheckedResource> resources;

    public CheckedResourcesResult(Integer code, String msg){
        super(code, msg);
    }
    public CheckedResourcesResult(Integer code, String msg, List<CheckedResource> resources){
        super(code, msg);
        this.resources = resources;
    }

    public static CheckedResourcesResult fail(String msg){
        return new CheckedResourcesResult(1, msg);
    }
    public static CheckedResourcesResult success(List<CheckedResource> checkedResources){
        return new CheckedResourcesResult(0, "", checkedResources);
    }
}
