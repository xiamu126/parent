package com.sybd.znld.model.onenet.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class OneNetExecuteResult extends BaseResult {
    public List<ObjectResource> data;

    @Getter @Setter
    public static class ObjectResource {
        public Integer obj_inst_id;
        public List<Resource> res;
    }
    @Getter @Setter
    public static class Resource {
        public Integer res_id;
        public Object val;
    }

    public boolean isOk(){
        return this.errno != null && this.errno == 0;
    }

    public boolean isEmpty(){
        if(data == null || data.size() <= 0) return true;
        for(var d : data){
            if(d.res != null && d.res.size() > 0){
                return false;
            }
        }
        return true;
    }

    public OneNetExecuteResult(){}
    public OneNetExecuteResult(int code, String msg){
        this.errno = code;
        this.error = msg;
    }
}
