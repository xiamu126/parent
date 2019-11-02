package com.sybd.znld.ministar.model;

import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class SubtitleForRegion extends Subtitle implements Serializable {
    public String regionId;
    public Long beginTimestamp;
    public Long endTimestamp;

    public boolean isValid(){
        if(!MyString.isUuid(regionId)) return false;
        //if(!MyDateTime.isAllFutureAndStrict(beginTimestamp, endTimestamp)) return false;
        if(!MyDateTime.isFuture(endTimestamp)) return false;
        return super.isValid();
    }

    @Override
    public String toString() { // 1567138545,1567673548,1,10,3,5500000000EE00EE00
        StringBuilder builder = new StringBuilder();
        builder.append(this.beginTimestamp / 1000); // 毫秒时间戳转换为秒时间戳
        builder.append(",");
        builder.append(this.endTimestamp / 1000); // 毫秒时间戳转换为秒时间戳
        builder.append(",");
        builder.append(super.toString());
        return builder.toString();
    }
}
