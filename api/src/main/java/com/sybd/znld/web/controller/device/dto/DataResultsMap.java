package com.sybd.znld.web.controller.device.dto;

import com.sybd.znld.model.BaseApiResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;

import java.util.Map;

@ApiModel(value = "通用查询返回值")
@NoArgsConstructor
public class DataResultsMap extends BaseApiResult {
    @ApiModelProperty(value = "具体的数值")
    public Map<String, Map<String, String>> value;

    public DataResultsMap(int code, String msg){
        super(code, msg);
    }
    public DataResultsMap(int code, String msg, Map<String, Map<String, String>> value){
        super(code, msg);
        this.value = value;
    }

    public static DataResultsMap fail(String msg){
        return new DataResultsMap(1, msg);
    }
    public static DataResultsMap success(Map<String, Map<String, String>> v){
        return new DataResultsMap(0, "", v);
    }

    @Override
    public boolean isOk() {
        if(value == null || value.size() <= 0) return false;
        return super.isOk();
    }
}
