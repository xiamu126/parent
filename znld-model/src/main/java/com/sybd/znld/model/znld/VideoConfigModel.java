package com.sybd.znld.model.znld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class VideoConfigModel implements Serializable {
    public String id;
    public String rtspUrl;
    public String rtmpUrl;
    public Boolean recordAudio;
    public String organizationId;
}
