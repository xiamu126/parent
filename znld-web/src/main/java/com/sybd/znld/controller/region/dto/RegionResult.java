package com.sybd.znld.controller.region.dto;

import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.service.znld.dto.RegionIdAndName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
