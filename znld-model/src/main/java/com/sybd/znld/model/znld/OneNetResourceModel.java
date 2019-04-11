package com.sybd.znld.model.znld;

import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.util.MyString;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class OneNetResourceModel implements Serializable {
    public String id;
    public Integer objId;
    public Integer objInstId;
    public Integer resId;
    public String value;
    public String description;
    public Short timeout = 5;
    public Short type;
    public Short status = Status.Monitor;

    public boolean isValidBeforeInsert(){
        if(objId == null || objInstId == null || resId == null || type == null ||status == null) return false;
        if(!Type.isValid(type)) return false;
        if(!Status.isValid(status)) return false;
        if(type == Type.Command && MyString.isEmptyOrNull(value)) return false; // 如果时命令，value不能为空
        return true;
    }

    public OneNetKey toOneNetKey(){
        return OneNetKey.from(objId, objInstId, resId);
    }

    public static class Type{
        public static final short Command = 0;
        public static final short Value = 1;
        public static final short Unit = 2;
        public static final short State = 3;
        public static final short Other = 4;

        public static boolean isValid(short v){
            switch (v){
                case Command: case Value: case Unit: case State: case Other: return true;
                default: return false;
            }
        }
    }

    public static class Status{
        public static final short Monitor = 0;
        public static final short Skip = 1;

        public static boolean isValid(short v){
            switch (v){
                case Monitor: case Skip: return true;
                default: return false;
            }
        }
    }
}
