package com.sybd.znld.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class VideoConfigEntity implements Serializable {
    private Long id;
    private String rtspUrl;
    private String rtmpUrl;
    private Boolean recordAudio;
    private String cameraId;
    private String clientId;
}
