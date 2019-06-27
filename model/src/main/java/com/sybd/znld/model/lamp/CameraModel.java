package com.sybd.znld.model.lamp;

import com.sybd.znld.util.MyString;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class CameraModel implements Serializable {
    public String id;
    public String rtspUrl;
    public String rtmp;
    public String flvUrl;
    public Boolean recordAudio = false;
    public Short status = Status.ENABLED;

    public static class Status{
        public static final short ENABLED = 0;
        public static final short DISABLED = 1;
    }
    public static class Rtmp{
        public String liveUrl;
        public String trackUrl;
    }

    public boolean isValid(){
        return MyString.isUuid(id) && !MyString.isAnyEmptyOrNull(rtspUrl, rtmp, flvUrl);
    }
    public boolean isValidForInsert(){
        return !MyString.isAnyEmptyOrNull(rtspUrl, rtmp, flvUrl);
    }
}
