package com.sybd.znld.onenet.dto;

import com.sybd.znld.onenet.OneNetConfig;
import com.whatever.util.MyString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ApiModel(value = "命令执行参数")
public class OneNetExecuteArgsEx extends OneNetExecuteArgs{
    @ApiModelProperty(hidden = true)
    public List<String> theCommands = Arrays.asList(OneNetConfig.ExecuteCommand.ZNLD_QX_UPLOAD_RATE.value,
            OneNetConfig.ExecuteCommand.ZNLD_LOCATION_UPLOAD_RATE.value,
            OneNetConfig.ExecuteCommand.ZNLD_STATUS_UPLOAD_RATE.value);
    @ApiModelProperty(value = "额外的参数")
    public Integer other = null;

    public OneNetExecuteArgsEx(String cmd){
        super(cmd);
    }
    public OneNetExecuteArgsEx(String cmd, Integer other){
        super(cmd);
        this.other = other;
    }
    public boolean verify(){
        if(this.other != null) {
            if(this.other <= 0 || this.other > 100) return false;
            return theCommands.contains(this.args);
        }
        return true;
    }
    @ApiModelProperty(hidden = true)
    public String getArgsEx() {
        if(!verify()) return "";
        if(this.other != null){
            String pv = this.other.toString();
            if(this.other >=1 && this.other <= 9) pv = "0"+pv;
            return this.args+pv;
        }
        return this.args;
    }
};