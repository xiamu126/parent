package com.sybd.znld.model.onenet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OfflineExecuteResult extends BaseResult {
    public Data data;
    @Getter @Setter
    public static class Data{
        public String uuid;
    }
    public OfflineExecuteResult() {}
    public OfflineExecuteResult(int code, String msg){
        super(code, msg);
    }
}
