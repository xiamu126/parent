package com.sybd.znld.web.controller.device.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.BaseApiResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@ApiModel(value = "获取设备历史数据的返回值，并按时间格式化")
@NoArgsConstructor
@Getter @Setter
public class PrettyHistoryDataResult extends BaseApiResult {
    @ApiModelProperty(value = "具体的历史数据")
    @JsonProperty("data_points")
    public Map<LocalDateTime, Double> dataPoints;

    public PrettyHistoryDataResult(Integer code, String msg){
        super(code, msg);
    }
    public PrettyHistoryDataResult(Integer code, String msg, Map<LocalDateTime, Double> dataPoints){
        super(code, msg);
        this.dataPoints = dataPoints;
    }

    public static PrettyHistoryDataResult fail(String msg){
        return new PrettyHistoryDataResult(1, msg);
    }
    public static PrettyHistoryDataResult success(Map<LocalDateTime, Double> result){
        return new PrettyHistoryDataResult(0,"",result);
    }
}
