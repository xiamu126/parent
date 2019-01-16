package com.sybd.znld.onenet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class OneNetExecuteResult {
    private Integer errno;
    private String error;
    private ObjectResource data;

    @Getter
    @Setter
    @ToString
    public static class ObjectResource {
        private Integer obj_inst_id;
        private Resource res;

        @Getter
        @Setter
        @ToString
        public static class Resource {
            private Integer res_id;
            private Object val;
        }
    }

    public boolean isOk(){
        return this.errno != null && this.errno == 0;
    }
}
