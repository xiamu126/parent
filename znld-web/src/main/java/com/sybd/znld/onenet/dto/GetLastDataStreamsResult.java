package com.sybd.znld.onenet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetLastDataStreamsResult extends BaseResult{
    public Data data;
    public boolean isOk(){
        return super.errno == 0 && !data.getDevices().isEmpty();
    }

    public static class DataStream{
        public String at;
        public String id;
        public Object value;

        public DataStream(String at, String id, Object value) {
            this.at = at;
            this.id = id;
            this.value = value;
        }

        public String getAt() {
            return at;
        }

        public void setAt(String at) {
            this.at = at;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
    public static class Device{
        public String title;
        public String id;
        @JsonProperty("datastreams")
        public List<DataStream> dataStreams;

        public Device(String title, String id, List<DataStream> dataStreams) {
            this.title = title;
            this.id = id;
            this.dataStreams = dataStreams;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<DataStream> getDataStreams() {
            return dataStreams;
        }

        public void setDataStreams(List<DataStream> dataStreams) {
            this.dataStreams = dataStreams;
        }
    }
    public static class Data{
        public List<Device> devices;

        public Data(List<Device> devices) {
            this.devices = devices;
        }

        public List<Device> getDevices() {
            return devices;
        }

        public void setDevices(List<Device> devices) {
            this.devices = devices;
        }
    }

    public GetLastDataStreamsResult(){}
    public GetLastDataStreamsResult(Integer errno, String error, Data data) {
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
