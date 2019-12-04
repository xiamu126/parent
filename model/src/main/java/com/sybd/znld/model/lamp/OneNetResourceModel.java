package com.sybd.znld.model.lamp;

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
    public Integer timeout = 5;
    public Integer type;

    public boolean isValidBeforeInsert(){
        if(objId == null || objInstId == null || resId == null || type == null) return false;
        if(!Type.isValid(type)) return false;
        if(type == Type.Command && MyString.isEmptyOrNull(value)) return false; // 如果时命令，value不能为空
        return true;
    }

    public OneNetKey toOneNetKey(){
        return OneNetKey.from(objId, objInstId, resId);
    }

    public static class Type{
        public static final int Command = 0;
        public static final int Value = 1;
        public static final int Unit = 2;
        public static final int State = 3;
        public static final int Other = 4;

        public static boolean isValid(int v){
            switch (v){
                case Command: case Value: case Unit: case State: case Other: return true;
                default: return false;
            }
        }
    }


}
