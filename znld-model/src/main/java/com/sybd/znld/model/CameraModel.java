package com.sybd.znld.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class CameraModel implements Serializable {
    public String id;
    public String rtspUrl;
    public String rtmp;
    public String flvUrl;
    public Boolean recordAudio;
    public Short status = Status.ENABLED;

    public static class Status{
        public static final short ENABLED = 0;
        public static final short DISABLED = 1;
    }
    public static class Rtmp{
        public String liveUrl;
        public String trackUrl;
    }
}
