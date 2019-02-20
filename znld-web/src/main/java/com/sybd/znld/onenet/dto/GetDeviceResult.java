package com.sybd.znld.onenet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class GetDeviceResult extends BaseResult{
    public Data data;

    public boolean isOk(){
        return super.errno == 0 && this.data != null && !data.dataStreams.isEmpty();
    }

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

        public Data(Boolean thePrivate, LocalDateTime createTime, String title, String protocol, Boolean online, Integer id, String imsi, List<DataStream> dataStreams) {
            this.thePrivate = thePrivate;
            this.createTime = createTime;
            this.title = title;
            this.protocol = protocol;
            this.online = online;
            this.id = id;
            this.imsi = imsi;
            this.dataStreams = dataStreams;
        }

        public Boolean getThePrivate() {
            return thePrivate;
        }

        public void setThePrivate(Boolean thePrivate) {
            this.thePrivate = thePrivate;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public Boolean getOnline() {
            return online;
        }

        public void setOnline(Boolean online) {
            this.online = online;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getImsi() {
            return imsi;
        }

        public void setImsi(String imsi) {
            this.imsi = imsi;
        }

        public List<DataStream> getDataStreams() {
            return dataStreams;
        }

        public void setDataStreams(List<DataStream> dataStreams) {
            this.dataStreams = dataStreams;
        }
    }
    public static class DataStream{
        @JsonProperty("create_time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        public LocalDateTime createTime;
        public String uuid;
        public String id;

        public DataStream(LocalDateTime createTime, String uuid, String id) {
            this.createTime = createTime;
            this.uuid = uuid;
            this.id = id;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public GetDeviceResult(Integer errno, String error, Data data) {
        super(errno, error);
        this.data = data;
    }
}
