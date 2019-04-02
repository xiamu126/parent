package com.sybd.znld.model.znld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class LampResourceModel implements Serializable {
    public String id;
    public String lampId;
    public String oneNetResourceId;
    public Short status;

    public static class Status{
        public static final short Monitor = 0;
        public static final short Skip = 1;

        public boolean isValid(short v){
            switch (v){
                case Monitor: case Skip: return true;
                default: return false;
            }
        }
    }
}
