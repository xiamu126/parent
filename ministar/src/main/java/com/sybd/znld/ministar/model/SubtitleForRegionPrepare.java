package com.sybd.znld.ministar.model;

import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyString;

import java.io.Serializable;
import java.util.List;

public class SubtitleForRegionPrepare extends Subtitle implements Serializable {

    public static class BeginEndTime{
        public Long beginTimestamp;
        public Long endTimestamp;
    }
    public List<BeginEndTime> times;
    public String regionId;

    public boolean isValid(){
        if(!MyString.isUuid(regionId)) return false;
        if(times == null || times.isEmpty()) return false;
        for(var t : times){
            if(!MyDateTime.isAllFutureAndStrict(t.beginTimestamp, t.endTimestamp)) return false;
        }
        return super.isValid();
    }
}
