package com.sybd.znld.web.controller.region.dto;

import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.dto.RegionIdAndName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel(value = "获取所有有效区域的返回值")
@Getter @Setter
public class RegionResult extends BaseApiResult {
    @ApiModelProperty(value = "具体的区域id和名字")
    public List<RegionIdAndName> regions;

    public RegionResult(int code, String msg){
        super(code, msg);
        regions = null;
    }
    public RegionResult(int code, String msg, List<RegionIdAndName> result){
        super(code, msg);
        regions = result;
    }
    public static RegionResult fail(String msg){
        return new RegionResult(1, msg);
    }
    public static RegionResult success(List<RegionIdAndName> result){
        return new RegionResult(0, "", result);
    }
}
