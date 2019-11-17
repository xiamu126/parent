package com.sybd.znld.model.lamp;

import com.sybd.znld.model.Status;
import com.sybd.znld.util.MyString;
import io.swagger.models.auth.In;
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
    public Integer status = Status.OK.getValue();

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
