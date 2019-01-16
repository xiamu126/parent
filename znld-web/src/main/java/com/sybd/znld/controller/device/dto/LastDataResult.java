package com.sybd.znld.controller.device.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.onenet.dto.GetDataStreamByIdResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "获取设备当前最新数据的返回值")
public class LastDataResult extends BaseApiResult {
    @ApiModelProperty(value = "最后的更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "CN")
    private LocalDateTime updateAt;
    @ApiModelProperty(value = "当前的数值")
    private String currentValue;

    public static LastDataResult fail(String msg){
        var tmp = new LastDataResult();
        tmp.setCode(1);
        tmp.setMsg(msg);
        return tmp;
    }

    public static LastDataResult success(GetDataStreamByIdResult result){
        var propertyMap = new PropertyMap<GetDataStreamByIdResult, LastDataResult>() {
            @Override
            protected void configure() {
                map(source.getData().getUpdateAt(), destination.getUpdateAt());
                map(source.getData().getCurrentValue(), destination.getCurrentValue());
                skip(destination.getCode());
                skip(destination.getMsg());
            }
        };
        var modelMapper = new ModelMapper();
        modelMapper.addMappings(propertyMap);
        modelMapper.validate();
        var tmp = modelMapper.map(result, LastDataResult.class);
        tmp.setCode(0);
        return tmp;
    }
}
