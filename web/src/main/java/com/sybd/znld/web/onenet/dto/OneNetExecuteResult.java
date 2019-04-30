package com.sybd.znld.web.onenet.dto;

import com.sybd.znld.web.onenet.dto.BaseResult;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OneNetExecuteResult extends BaseResult {
    public ObjectResource data;

    @Getter @Setter
    public static class ObjectResource {
        public Integer obj_inst_id;
        public Resource res;
    }
    @Getter @Setter
    public static class Resource {
        public Integer res_id;
        public Object val;
    }

    public boolean isOk(){
        return this.errno != null && this.errno == 0;
    }
}
