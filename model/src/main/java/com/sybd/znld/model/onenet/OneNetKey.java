package com.sybd.znld.model.onenet;

import com.sybd.znld.model.IValid;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;

import java.io.Serializable;
import java.util.regex.Pattern;

public class OneNetKey implements IValid, Serializable{
    public Integer objId;
    public Integer objInstId;
    public Integer resId;

    public static final String DATA_STREAM_ID_REGEX = "^\\d+_\\d+_\\d+$";
    public static boolean isDataStreamId(String dataStreamId){
        return !MyString.isEmptyOrNull(dataStreamId) && dataStreamId.matches(DATA_STREAM_ID_REGEX);
    }

    @Override
    public boolean isValid(){
        return MyNumber.isPositive(objId) && MyNumber.isPositiveOrZero(objInstId) && MyNumber.isPositive(resId);
    }

    public String toDataStreamId(){
        return this.objId+"_"+this.objInstId+"_"+this.resId;
    }

    public static String toDataStreamId(Integer objId, Integer objInstId, Integer resId){
        return objId+"_"+objInstId+"_"+resId;
    }
    public static OneNetKey from(String dataStreamId){
        var ret = Pattern.compile("^(\\d+)_(\\d+)_(\\d+)$").matcher(dataStreamId);
        if(ret.find()){
            var key = new OneNetKey();
            key.objId = Integer.parseInt(ret.group(1));
            key.objInstId = Integer.parseInt(ret.group(2));
            key.resId = Integer.parseInt(ret.group(3));
            return key;
        }
        return null;
    }
    public static OneNetKey from(Integer objId, Integer objInstId, Integer resId){
        var key = new OneNetKey();
        key.objId = objId;
        key.objInstId = objInstId;
        key.resId = resId;
        return key;
    }
}
