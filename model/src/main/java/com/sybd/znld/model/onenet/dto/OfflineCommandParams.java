package com.sybd.znld.model.onenet.dto;

import com.sybd.znld.util.MyDateTime;

import java.time.LocalDateTime;

public class OfflineCommandParams extends CommandParams {
    public LocalDateTime expiredTime = LocalDateTime.now().plusMinutes(5); // 默认为5分钟后过期；

    @Override
     public String toUrlString(){
        var tmp = super.toUrlString();
        tmp += "&expired_time=" + MyDateTime.format(expiredTime, MyDateTime.ISO8601);
        return tmp;
    }
}
