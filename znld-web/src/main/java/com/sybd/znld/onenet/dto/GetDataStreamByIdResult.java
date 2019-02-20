package com.sybd.znld.onenet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class GetDataStreamByIdResult extends BaseResult{
    public Data data;
    public boolean isOk(){
        return super.errno == 0;
    }

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

        public Data(){}
        public Data(LocalDateTime updateAt, String id, LocalDateTime createTime, Object currentValue) {
            this.updateAt = updateAt;
            this.id = id;
            this.createTime = createTime;
            this.currentValue = currentValue;
        }

        public LocalDateTime getUpdateAt() {
            return updateAt;
        }

        public void setUpdateAt(LocalDateTime updateAt) {
            this.updateAt = updateAt;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }

        public Object getCurrentValue() {
            return currentValue;
        }

        public void setCurrentValue(Object currentValue) {
            this.currentValue = currentValue;
        }
    }

    public GetDataStreamByIdResult(){}
    public GetDataStreamByIdResult(Integer errno, String error, Data data) {
        super(errno, error);
        this.data = data;
    }
}
