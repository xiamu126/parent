package com.sybd.znld.model.onenet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class GetDataStreamByIdResult extends BaseResult {
    public Data data;
    public boolean isOk(){
        return super.errno == 0;
    }

    @Getter @Setter
    public static class Data{
        @JsonProperty("update_at")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "CN")
        public LocalDateTime updateAt;
        public String id;
        @JsonProperty("create_time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        public LocalDateTime createTime;
        @JsonProperty("current_value")
        public Object currentValue;
    }
}
