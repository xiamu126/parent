package com.sybd.znld.web.controller.device.dto;

import com.sybd.znld.model.BaseApiResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@ApiModel(value = "获取设备当前最新数据的返回值")
@NoArgsConstructor
@Getter
@Setter
public class LastDataResults extends BaseApiResult {
    @ApiModelProperty(value = "具体的数值")
    public Map<String, LastData> value;

    public LastDataResults(Integer code, String msg){
        super(code, msg);
    }
    public LastDataResults(Integer code, String msg, Map<String, LastData> value){
        super(code, msg);
        this.value = value;
    }

    public static LastDataResults fail(String msg){
        return new LastDataResults(1, msg);
    }
    public static LastDataResults success(Map<String, LastData> v){
        return new LastDataResults(0, "", v);
    }
}
