package com.sybd.znld.onenet.dto;

import com.sybd.znld.onenet.dto.OneNetKey;
import com.whatever.util.MyString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CommonParams {
    private Integer deviceId;
    private String imei;
    private int objId;
    private int resId;
    private int objInstId;
    private int timeout;

    public CommonParams(Integer deviceId, String imei, OneNetKey key, int timeout){
        this.deviceId = deviceId;
        this.imei = imei;
        this.objId = key.getObjId();
        this.resId = key.getResId();
        this.objInstId = key.getObjInstId();
        this.timeout = timeout;
    }

    public String toUrlString(){
        String params = "?imei={}&obj_id={}&res_id={}&obj_inst_id={}&timeout={}";
        params = MyString.replace(params, imei,
                String.valueOf(objId),
                String.valueOf(resId),
                String.valueOf(objInstId),
                String.valueOf(timeout));
        return params;
    }
}
