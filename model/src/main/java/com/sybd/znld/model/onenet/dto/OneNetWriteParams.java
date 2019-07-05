package com.sybd.znld.model.onenet.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class OneNetWriteParams implements Serializable {
    public List<Data> data = new ArrayList<>();

    @Getter @Setter
    public static class Data{
        public Integer res_id;
        public Integer type;
        public Object val;
    }

    public OneNetWriteParams(Integer resId, Integer type, Object val){
        var tmp = new Data();
        tmp.res_id = resId;
        tmp.type = type;
        tmp.val = val;
        this.data.add(tmp);
    }
}
