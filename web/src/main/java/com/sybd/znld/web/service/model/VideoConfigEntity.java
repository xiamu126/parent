package com.sybd.znld.znld.service.model;

import java.io.Serializable;

public class VideoConfigEntity implements Serializable {
    public Long id;
    public String rtspUrl;
    public String rtmpUrl;
    public Boolean recordAudio;
    public String cameraId;
    public String clientId;

    public VideoConfigEntity(Long id, String rtspUrl, String rtmpUrl, Boolean recordAudio, String cameraId, String clientId) {
        this.id = id;
        this.rtspUrl = rtspUrl;
        this.rtmpUrl = rtmpUrl;
        this.recordAudio = recordAudio;
        this.cameraId = cameraId;
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
