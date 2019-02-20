package com.sybd.znld.onenet.dto;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OneNetKey implements Serializable{
    public Integer objId;
    public Integer objInstId;
    public Integer resId;

    public String toDataStreamId(){
        return this.objId+"_"+this.objInstId+"_"+this.resId;
    }

    public static String toDataStreamId(Integer objId, Integer objInstId, Integer resId){
        return objId+"_"+objInstId+"_"+resId;
    }
    public static OneNetKey from(String id){
        String regex = "^\\d+_\\d+_\\d+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(id);
        if (!matcher.matches()) return null;
        String[] array = id.split("_");
        return new OneNetKey(Integer.parseInt(array[0]), Integer.parseInt(array[1]), Integer.parseInt(array[2]));
    }

    public OneNetKey(){}
    public OneNetKey(Integer objId, Integer objInstId, Integer resId) {
        this.objId = objId;
        this.objInstId = objInstId;
        this.resId = resId;
    }

    public Integer getObjId() {
        return objId;
    }

    public void setObjId(Integer objId) {
        this.objId = objId;
    }

    public Integer getObjInstId() {
        return objInstId;
    }

    public void setObjInstId(Integer objInstId) {
        this.objInstId = objInstId;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }
}
