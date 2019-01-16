package com.sybd.znld.onenet.dto;

import lombok.*;

import java.io.Serializable;
import java.util.regex.Pattern;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class OneNetKey implements Serializable {
    private Integer objId;
    private Integer objInstId;
    private Integer resId;
    public String toDataStreamId(){
        return objId+"_"+objInstId+"_"+resId;
    }
    public static String toDataStreamId(Integer objId, Integer objInstId, Integer resId){
        return objId+"_"+objInstId+"_"+resId;
    }
    public static OneNetKey from(String id){
        assert id != null;
        var regex = "^\\d+_\\d+_\\d+$";
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(id);
        if (!matcher.matches()) throw new AssertionError();
        var array = id.split("_");
        return new OneNetKey(Integer.parseInt(array[0]), Integer.parseInt(array[1]), Integer.parseInt(array[2]));
    }
}
