package com.sybd.znld.web.controller.device.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.onenet.dto.GetDataStreamByIdResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@ApiModel(value = "获取设备当前最新数据的返回值")
@NoArgsConstructor
@Getter @Setter
public class LastDataResult extends BaseApiResult {
    @ApiModelProperty(value = "最后的更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "CN")
    public LocalDateTime updateAt;
    @ApiModelProperty(value = "当前的数值")
    public String currentValue;

    public LastDataResult(Integer code, String msg){
        super(code, msg);
    }
    public LastDataResult(Integer code, String msg, LocalDateTime updateAt, String currentValue){
        super(code, msg);
        this.updateAt = updateAt;
        this.currentValue = currentValue;
    }

    public static LastDataResult fail(String msg){
        return new LastDataResult(1, msg);
    }
    public static LastDataResult success(GetDataStreamByIdResult result){
        var lastDataResult = new LastDataResult();
        lastDataResult.updateAt = result.data.updateAt;
        lastDataResult.currentValue = result.data.currentValue.toString();
        lastDataResult.code = 0;
        lastDataResult.msg = "";
        return lastDataResult;
    }
}
