package com.sybd.znld.model.znld;

import com.sybd.znld.model.onenet.OneNetKey;
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
    public String  description;
    public Short timeout;
    public Short type;

    public OneNetKey toOneNetKey(){
        return OneNetKey.from(objId, objInstId, resId);
    }

    public static class Type{
        public static final short Command = 0;
        public static final short Value = 1;
        public static final short Unit = 2;
        public static final short State = 3;
        public static final short Other = 4;

        public boolean isValid(short v){
            switch (v){
                case Command: case Value: case Unit: case State: case Other: return true;
                default: return false;
            }
        }
    }
}
