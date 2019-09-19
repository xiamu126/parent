package com.sybd.znld.onenet.controller.device.dto;

import com.sybd.znld.model.BaseApiResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;

import java.util.Map;

@ApiModel(value = "通用查询返回值")
@NoArgsConstructor
public class DataResults extends BaseApiResult {
    @ApiModelProperty(value = "具体的数值")
    public Map<String, String> value;

    public DataResults(int code, String msg){
        super(code, msg);
    }
    public DataResults(int code, String msg, Map<String, String> value){
        super(code, msg);
        this.value = value;
    }

    public static DataResults fail(String msg){
        return new DataResults(1, msg);
    }
    public static DataResults success(Map<String, String> v){
        return new DataResults(0, "", v);
    }

    @Override
    public boolean isOk() {
        if(value == null || value.size() <= 0) return false;
        return super.isOk();
    }
}
