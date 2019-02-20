package com.sybd.znld.onenet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class GetDataStreamsByIdsResult extends BaseResult{
    public List<Data> data;

    public boolean isOk(){
        return super.errno == 0 && this.data != null && !data.isEmpty();
    }

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

    public GetDataStreamsByIdsResult(){}
    public GetDataStreamsByIdsResult(Integer errno, String error, List<Data> data) {
        super(errno, error);
        this.data = data;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
