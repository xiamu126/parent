package com.sybd.znld.controller.device.dto;

import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.service.dto.CheckedResource;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.var;

import java.util.List;

@Getter
@Setter
@ToString
@ApiModel(value = "获取选中资源的返回值")
public class CheckedResourcesResult extends BaseApiResult {
    private List<CheckedResource> checkedResources;
    public static CheckedResourcesResult fail(String msg){
        var tmp = new CheckedResourcesResult();
        tmp.setCode(0);
        tmp.setMsg(msg);
        return tmp;
    }
    public static CheckedResourcesResult success(List<CheckedResource> result){
        var tmp = new CheckedResourcesResult();
        tmp.setCode(1);
        tmp.setCheckedResources(result);
        return tmp;
    }
}
