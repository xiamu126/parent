package com.sybd.znld.web.controller.device.dto;

import com.sybd.znld.model.BaseApiResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;

import java.util.Map;

@ApiModel(value = "最新数据返回值")
@NoArgsConstructor
public class LastDataResultsMap extends BaseApiResult {
    @ApiModelProperty(value = "具体的数值")
    public Map<String, Map<String, LastData>> value;

    public LastDataResultsMap(int code, String msg){
        super(code, msg);
    }
    public LastDataResultsMap(int code, String msg, Map<String, Map<String, LastData>> value){
        super(code, msg);
        this.value = value;
    }

    public static LastDataResultsMap fail(String msg){
        return new LastDataResultsMap(1, msg);
    }
    public static LastDataResultsMap success(Map<String, Map<String, LastData>> v){
        return new LastDataResultsMap(0, "", v);
    }

    @Override
    public boolean isOk() {
        if(value == null || value.size() <= 0) return false;
        return super.isOk();
    }
}
