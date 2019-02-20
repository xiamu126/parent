package com.sybd.znld.onenet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetHistoryDataStreamResult extends BaseResult {
    public Data data;

    public boolean isOk(){
        return super.errno == 0 && data != null && data.count > 0;
    }

    public static class Data{
        public String cursor;
        public Integer count;
        @JsonProperty("datastreams")
        public List<DataStream> dataStreams;

        public Data(){}
        public Data(String cursor, Integer count, List<DataStream> dataStreams) {
            this.cursor = cursor;
            this.count = count;
            this.dataStreams = dataStreams;
        }

        public String getCursor() {
            return cursor;
        }

        public void setCursor(String cursor) {
            this.cursor = cursor;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public List<DataStream> getDataStreams() {
            return dataStreams;
        }

        public void setDataStreams(List<DataStream> dataStreams) {
            this.dataStreams = dataStreams;
        }
    }
    public static class DataStream{
        public String id;
        @JsonProperty("datapoints")
        public List<DataPoint> dataPoints;

        public DataStream(String id, List<DataPoint> dataPoints) {
            this.id = id;
            this.dataPoints = dataPoints;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<DataPoint> getDataPoints() {
            return dataPoints;
        }

        public void setDataPoints(List<DataPoint> dataPoints) {
            this.dataPoints = dataPoints;
        }
    }
    public static class DataPoint{
        public String at;
        public String value;

        public DataPoint(String at, String value) {
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

    public GetHistoryDataStreamResult(){}
    public GetHistoryDataStreamResult(Integer errno, String error, Data data) {
        super(errno, error);
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
