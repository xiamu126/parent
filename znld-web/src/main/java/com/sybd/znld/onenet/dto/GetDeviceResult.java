package com.sybd.znld.onenet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
public class GetDeviceResult extends BaseResult {

    public boolean isOk(){
        return errno == 0 && data != null && data.dataStreams != null && data.dataStreams.length > 0;
    }

    private Data data;

    @Getter @Setter @ToString
    public static class Data{
        @JsonProperty("private")
        private Boolean thePrivate;
        @JsonProperty("create_time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        private String title;
        private String protocol;
        private Boolean online;
        private Integer id;
        private String imsi;

        @JsonProperty("datastreams")
        private DataStream[] dataStreams;

        @Getter @Setter @ToString
        public static class DataStream{
            @JsonProperty("create_time")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime createTime;
            private String uuid;
            private String id;
        }
    }
}
