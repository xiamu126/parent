package com.sybd.znld.model.onenet.dto;

import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyString;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class CommandParams{
    public String imei;
    public OneNetKey oneNetKey;
    public Integer timeout;
    public String command;
    public LocalDateTime expiredTime = LocalDateTime.now().plusMinutes(5); // 默认为5分钟后过期；

    public CommandParams(String imei, OneNetKey oneNetKey, String command){
        this.imei = imei;
        this.oneNetKey = oneNetKey;
        this.command = command;
    }
    public CommandParams(String imei, OneNetKey oneNetKey, String command, Integer timeout){
        this.imei = imei;
        this.oneNetKey = oneNetKey;
        this.command = command;
        this.timeout = timeout;
    }

    public String toUrlString(){
        var tmp = "?imei="+this.imei+"&obj_id="+this.oneNetKey.objId+"&res_id="+this.oneNetKey.resId+"&obj_inst_id="+this.oneNetKey.objInstId;
        if(timeout != null && timeout > 0){
            tmp = tmp +"&timeout="+this.timeout;
        }
        return tmp;
    }

    public String toOfflineUrlString(){
        var tmp = this.toUrlString();
        tmp += "&expired_time=" + MyDateTime.format(expiredTime, MyDateTime.ISO8601);
        return tmp;
    }
}
