package com.sybd.znld.web.onenet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.web.onenet.dto.BaseResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class GetLastDataStreamsResult extends BaseResult {
    public Data data;
    public boolean isOk(){
        return super.errno == 0 && !data.getDevices().isEmpty();
    }

    @Getter @Setter
    public static class DataStream{
        public String at;
        public String id;
        public Object value;
    }
    @Getter @Setter
    public static class Device{
        public String title;
        public String id;
        @JsonProperty("datastreams")
        public List<DataStream> dataStreams;
    }
    @Getter @Setter
    public static class Data{
        public List<Device> devices;
    }
}
