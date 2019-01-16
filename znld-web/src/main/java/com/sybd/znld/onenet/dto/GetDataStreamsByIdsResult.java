package com.sybd.znld.onenet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
public class GetDataStreamsByIdsResult extends BaseResult {
    public boolean isOk(){
        return errno == 0 && data != null && data.length > 0;
    }
    private Data[] data;

    @Getter @Setter @ToString
    public static class Data{
        @JsonProperty("update_at")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "CN")
        private LocalDateTime updateAt;
        private String id;
        @JsonProperty("create_time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
        @JsonProperty("current_value")
        private Object currentValue;
    }
}
