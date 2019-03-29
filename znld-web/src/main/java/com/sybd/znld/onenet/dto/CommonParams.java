package com.sybd.znld.onenet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommonParams{
    public Integer deviceId;
    public String imei;
    public Integer objId;
    public Integer resId;
    public Integer objInstId;
    public Integer timeout;
    public String toUrlString(){
        return "?imei="+this.imei+"&obj_id="+this.objId+"&res_id="+this.resId+"&obj_inst_id="+this.objInstId+"&timeout="+this.timeout;
    }
}
