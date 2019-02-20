package com.sybd.znld.onenet.dto;

import java.io.Serializable;

public class OneNetExecuteResult extends BaseResult {
    public ObjectResource data;

    public static class ObjectResource {
        public Integer obj_inst_id;
        public Resource res;

        public ObjectResource(Integer obj_inst_id, Resource res) {
            this.obj_inst_id = obj_inst_id;
            this.res = res;
        }

        public Integer getObj_inst_id() {
            return obj_inst_id;
        }

        public void setObj_inst_id(Integer obj_inst_id) {
            this.obj_inst_id = obj_inst_id;
        }

        public Resource getRes() {
            return res;
        }

        public void setRes(Resource res) {
            this.res = res;
        }
    }
    public static class Resource {
        public Integer res_id;
        public Object val;

        public Resource(Integer res_id, Object val) {
            this.res_id = res_id;
            this.val = val;
        }

        public Integer getRes_id() {
            return res_id;
        }

        public void setRes_id(Integer res_id) {
            this.res_id = res_id;
        }

        public Object getVal() {
            return val;
        }

        public void setVal(Object val) {
            this.val = val;
        }
    }

    public boolean isOk(){
        return this.errno != null && this.errno == 0;
    }

    public OneNetExecuteResult(){}
    public OneNetExecuteResult(Integer errno, String error, ObjectResource data) {
        super(errno, error);
        this.data = data;
    }

    public ObjectResource getData() {
        return data;
    }

    public void setData(ObjectResource data) {
        this.data = data;
    }
}
