package com.sybd.znld.web.controller.device.dto;

import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.znld.util.MyString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;

@ApiModel(value = "通用查询返回值")
@NoArgsConstructor
public class DataResult extends BaseApiResult {
    @ApiModelProperty(value = "具体的数值")
    public String value;

    public DataResult(int code, String msg){
        super(code, msg);
    }
    public DataResult(int code, String msg, String value){
        super(code, msg);
        this.value = value;
    }

    public static DataResult fail(String msg){
        return new DataResult(1, msg);
    }
    public static DataResult success(String v){
        return new DataResult(0, "", v);
    }

    @Override
    public boolean isOk() {
        if(MyString.isEmptyOrNull(this.value)) return false;
        return super.isOk();
    }
}
