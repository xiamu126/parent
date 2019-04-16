package com.sybd.znld.onenet.dto;

import com.sybd.onenet.model.OneNetKey;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommandParams{
    public Integer deviceId;
    public String imei;
    public OneNetKey oneNetKey;
    public Short timeout;
    public String command;

    public String toUrlString(){
        return "?imei="+this.imei+"&obj_id="+this.oneNetKey.objId+"&res_id="+this.oneNetKey.resId+"&obj_inst_id="+this.oneNetKey.objInstId+"&timeout="+this.timeout;
    }
}
