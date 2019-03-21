package com.sybd.znld.service.znld.model;

import java.io.Serializable;

public class VideoConfigModel implements Serializable {
    public String id;
    public String rtspUrl;
    public String rtmpUrl;
    public Boolean recordAudio;
    public String organizationId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }

    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }

    public Boolean getRecordAudio() {
        return recordAudio;
    }

    public void setRecordAudio(Boolean recordAudio) {
        this.recordAudio = recordAudio;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
