package com.sybd.znld.model.onenet.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
public class OneNetWriteParams implements Serializable {
    public List<Data> data;

    @Getter @Setter
    public static class Data{
        public Integer res_id;
        public Integer type;
        public Object val;
    }
}
