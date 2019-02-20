package com.sybd.znld.controller.device.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.onenet.dto.GetDataStreamByIdResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.time.LocalDateTime;

@ApiModel(value = "获取设备当前最新数据的返回值")
public class LastDataResult extends BaseApiResult{
    @ApiModelProperty(value = "最后的更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "CN")
    public LocalDateTime updateAt;
    @ApiModelProperty(value = "当前的数值")
    public String currentValue;

    public LastDataResult(){}
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
        PropertyMap<GetDataStreamByIdResult, LastDataResult> propertyMap = new PropertyMap<GetDataStreamByIdResult, LastDataResult>(){
            @Override
            protected void configure() {
                map(source.data.updateAt, destination.updateAt);
                map(source.data.currentValue, destination.currentValue);
                skip(destination.code);
                skip(destination.msg);
            }
        };
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(propertyMap);
        modelMapper.validate();
        LastDataResult tmp = modelMapper.map(result, LastDataResult.class);
        tmp.code = 0;
        return tmp;
    }
}
