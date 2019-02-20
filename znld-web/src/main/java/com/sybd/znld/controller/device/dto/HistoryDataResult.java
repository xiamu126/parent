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
    @JsonProperty("datastreams")
    public List<DataStream> dataStreams;

    public static class DataStream{
        public String at;
        public String value;

        public DataStream(){}
        public DataStream(String at, String value) {
            this.at = at;
            this.value = value;
        }

        public String getAt() {
            return at;
        }

        public void setAt(String at) {
            this.at = at;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public HistoryDataResult(Integer code, String msg){
        super(code, msg);
    }
    public HistoryDataResult(Integer code, String msg, List<DataStream> dataStreams){
        super(code, msg);
        this.dataStreams = dataStreams;
    }

    public static HistoryDataResult fail(String msg){
        return new HistoryDataResult(1, msg);
    }
    public static HistoryDataResult success(GetHistoryDataStreamResult result){
        PropertyMap<GetHistoryDataStreamResult, HistoryDataResult> propertyMap = new PropertyMap<GetHistoryDataStreamResult, HistoryDataResult>(){
            @Override
            protected void configure() {
                map(source.getData().getCursor(), destination.cursor);
                map(source.getData().getDataStreams(), destination.dataStreams);
                skip(destination.getCode());
                skip(destination.getMsg());
            }
        };
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(propertyMap);
        modelMapper.validate();
        HistoryDataResult tmp = modelMapper.map(result, HistoryDataResult.class);
        tmp.code = 0;
        return tmp;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public List<DataStream> getDataStreams() {
        return dataStreams;
    }

    public void setDataStreams(List<DataStream> dataStreams) {
        this.dataStreams = dataStreams;
    }
}
