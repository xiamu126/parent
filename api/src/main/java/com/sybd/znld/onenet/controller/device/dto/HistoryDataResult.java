package com.sybd.znld.onenet.controller.device.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.onenet.dto.GetHistoryDataStreamResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@ApiModel(value = "获取设备历史数据的返回值")
@NoArgsConstructor
@Getter @Setter
public class HistoryDataResult extends BaseApiResult {
    @ApiModelProperty(value = "当前的游标位置")
    public String cursor;
    @ApiModelProperty(value = "具体的历史数据")
    @JsonProperty("data_points")
    public List<GetHistoryDataStreamResult.DataPoint> dataPoints;

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
        var historyDataResult = new HistoryDataResult();
        historyDataResult.cursor = result.data.cursor;
        historyDataResult.dataPoints = result.data.dataStreams.get(0).dataPoints;
        historyDataResult.code = 0;
        historyDataResult.msg = "";
        return historyDataResult;
    }
}
