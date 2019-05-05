package com.sybd.znld.web.onenet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.web.onenet.dto.BaseResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class GetDeviceResult extends BaseResult {
    public Data data;
    public boolean isOk(){
        return super.errno == 0 && this.data != null && !data.dataStreams.isEmpty();
    }

    @Getter @Setter
    public static class Data{
        @JsonProperty("private")
        public Boolean thePrivate;
        @JsonProperty("create_time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        public LocalDateTime createTime;
        public String title;
        public String protocol;
        public Boolean online;
        public Integer id;
        public String imsi;
        @JsonProperty("datastreams")
        public List<DataStream> dataStreams;
    }
    @Getter @Setter
    public static class DataStream{
        @JsonProperty("create_time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        public LocalDateTime createTime;
        public String uuid;
        public String id;
    }
}
