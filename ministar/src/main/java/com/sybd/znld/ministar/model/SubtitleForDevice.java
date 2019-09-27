package com.sybd.znld.ministar.model;

import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;

import java.time.LocalDateTime;
import java.util.List;

public class SubtitleForDevice {
    public String title;
    public String userId;
    public String deviceId;
    public Integer type;
    public List<SubtitleForRegion.Rgb> colors;
    public Integer speed;
    public Integer brightness;
    public LocalDateTime beginTime;
    public LocalDateTime endTime;

    public boolean isValid(){
        if(MyString.isEmptyOrNull(title)) return false;
        else if(!MyString.isUuid(userId)) return false;
        else if(!MyNumber.isPositive(deviceId)) return false;
        else if(!SubtitleForRegion.Type.isValid(type)) return false;
        else if(MyNumber.isPositive(speed)) return false;
        else if(brightness < 0 || brightness > 100) return false;
        else if(!MyDateTime.isAllFutureAndStrict(beginTime, endTime)) return false;
        else {
            for(var c : colors){
                if(!c.isValid()) return false;
            }
        }
        return true;
    }
}
