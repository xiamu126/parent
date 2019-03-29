package com.sybd.znld.onenet.dto;

import com.sybd.znld.onenet.OneNetService;
import com.sybd.znld.util.MyByte;
import com.sybd.znld.util.MyNumber;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ApiModel(value = "命令执行参数")
public class OneNetExecuteArgsEx extends OneNetExecuteArgs{
    @ApiModelProperty(hidden = true)
    public List<String> theCommands = Arrays.asList(OneNetService.ZNLD_QX_UPLOAD_RATE,
            OneNetService.ZNLD_LOCATION_UPLOAD_RATE, OneNetService.ZNLD_STATUS_UPLOAD_RATE, OneNetService.ZNLD_DD_EXECUTE);
    @ApiModelProperty(value = "额外的参数")
    public Object other = null;

    public OneNetExecuteArgsEx(String cmd){
        super(cmd);
    }
    public OneNetExecuteArgsEx(String cmd, Integer other){
        super(cmd);
        this.other = other;
    }
    public boolean verify(){
        if(this.args.equals(OneNetService.ZNLD_QX_UPLOAD_RATE) || this.args.equals(OneNetService.ZNLD_LOCATION_UPLOAD_RATE) || this.args.equals(OneNetService.ZNLD_STATUS_UPLOAD_RATE)){
            // 检查具体的速率，必须是[0,100)
            if(!MyNumber.isInteger(this.other)) return false;
            if((int)this.other <=0 || (int)this.other > 100) return false;
        }
        if(this.args.equals(OneNetService.ZNLD_DD_EXECUTE)){
            // 如果是灯带的，检查指令是否合法
            if(this.other == null) return false;
            var bytes = this.other.toString().getBytes();
            var DG = "DG".getBytes();
            if(!MyByte.equals(DG, Arrays.copyOfRange(bytes,0,2))){
                return false;
            }
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