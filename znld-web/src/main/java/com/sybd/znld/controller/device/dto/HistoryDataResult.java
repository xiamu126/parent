package com.sybd.znld.controller.device.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.onenet.dto.GetHistoryDataStreamResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.List;

@ApiModel(value = "获取设备历史数据的返回值")
public class HistoryDataResult extends BaseApiResult{
    @ApiModelProperty(value = "当前的游标位置")
    public String cursor;
    @ApiModelProperty(value = "具体的历史数据")
    @JsonProperty("data_points")
    public List<GetHistoryDataStreamResult.DataPoint> dataPoints;

    public HistoryDataResult(){}
    public HistoryDataResult(Integer code, String msg){
        super(code, msg);
    }
    public HistoryDataResult(Integer code, String msg, List<GetHistoryDataStreamResult.DataPoint> dataPoints){
        super(code, msg);
        this.dataPoints = dataPoints;
    }

    public static HistoryDataResult fail(String msg){
        return new HistoryDataResult(1, msg);
    }
    public static HistoryDataResult success(GetHistoryDataStreamResult result){
        PropertyMap<GetHistoryDataStreamResult, HistoryDataResult> propertyMap = new PropertyMap<GetHistoryDataStreamResult, HistoryDataResult>(){
            @Override
            protected void configure() {
                map(source.data.cursor, destination.cursor);
                skip(destination.dataPoints);
                skip(destination.code);
                skip(destination.msg);
            }
        };
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(propertyMap);
        modelMapper.validate();
        HistoryDataResult tmp = modelMapper.map(result, HistoryDataResult.class);
        tmp.dataPoints = result.data.dataStreams.get(0).dataPoints;
        tmp.code = 0;
        tmp.msg = "";
        return tmp;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public List<GetHistoryDataStreamResult.DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<GetHistoryDataStreamResult.DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
