package com.sybd.znld.web.onenet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.web.onenet.dto.BaseResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class GetHistoryDataStreamResult extends BaseResult {
    public Data data;
    public boolean isOk(){
        return super.errno == 0 && data != null && data.count > 0;
    }

    @Getter @Setter
    public static class Data{
        public String cursor;
        public Integer count;
        @JsonProperty("datastreams")
        public List<DataStream> dataStreams;
    }
    @Getter @Setter
    public static class DataStream{
        public String id;
        @JsonProperty("datapoints")
        public List<DataPoint> dataPoints;
    }
    @Getter @Setter
    public static class DataPoint{
        public String at;
        public String value;
    }
}
