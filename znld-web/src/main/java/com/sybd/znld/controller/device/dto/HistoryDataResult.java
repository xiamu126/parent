package com.sybd.znld.controller.device.dto;

import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.onenet.dto.GetHistoryDataStreamResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "获取设备历史数据的返回值")
public class HistoryDataResult extends BaseApiResult {
    @ApiModelProperty(value = "当前的游标位置")
    private String cursor;
    @ApiModelProperty(value = "具体的历史数据")
    private DataStream[] dataStreams;

    @Getter @Setter @ToString
    public static class DataStream{
        private String at;
        private String value;
    }

    public static HistoryDataResult fail(String msg){
        var tmp = new HistoryDataResult();
        tmp.setCode(1);
        tmp.setMsg(msg);
        return tmp;
    }

    public static HistoryDataResult success(GetHistoryDataStreamResult result){
        var propertyMap = new PropertyMap<GetHistoryDataStreamResult, HistoryDataResult>() {
            @Override
            protected void configure() {
                map(source.getData().getCursor(), destination.getCursor());
                map(source.getData().getDatastreams(), destination.getDataStreams());
                skip(destination.getCode());
                skip(destination.getMsg());
            }
        };
        var modelMapper = new ModelMapper();
        modelMapper.addMappings(propertyMap);
        modelMapper.validate();
        var tmp = modelMapper.map(result, HistoryDataResult.class);
        tmp.setCode(0);
        return tmp;
    }
}
