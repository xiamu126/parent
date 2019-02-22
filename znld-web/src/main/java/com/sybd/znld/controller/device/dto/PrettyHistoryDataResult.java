package com.sybd.znld.controller.device.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.onenet.dto.GetHistoryDataStreamResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.List;
import java.util.Map;

@ApiModel(value = "获取设备历史数据的返回值，并按时间格式化")
public class PrettyHistoryDataResult extends BaseApiResult {
    @ApiModelProperty(value = "具体的历史数据")
    @JsonProperty("data_points")
    public Map<Integer, String> dataPoints;

    public PrettyHistoryDataResult(){}
    public PrettyHistoryDataResult(Integer code, String msg){
        super(code, msg);
    }
    public PrettyHistoryDataResult(Integer code, String msg, Map<Integer, String> dataPoints){
        super(code, msg);
        this.dataPoints = dataPoints;
    }

    public static PrettyHistoryDataResult fail(String msg){
        return new PrettyHistoryDataResult(1, msg);
    }
    public static PrettyHistoryDataResult success(Map<Integer, String> result){
        return new PrettyHistoryDataResult(0,"",result);
    }

    public Map<Integer, String> getDataPoints() {
        return dataPoints;
    }
    public void setDataPoints(Map<Integer, String> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
