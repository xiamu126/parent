package com.sybd.znld.onenet.dto;

import com.sybd.znld.onenet.OneNetConfig;
import com.whatever.util.MyString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@ApiModel(value = "命令执行参数")
@Getter @Setter @NoArgsConstructor
public class OneNetExecuteArgsEx extends OneNetExecuteArgs {
    @ApiModelProperty(hidden = true)
    private List<String> theCommands = Arrays.asList(OneNetConfig.ExecuteCommand.ZNLD_QX_UPLOAD_RATE.getValue(),
            OneNetConfig.ExecuteCommand.ZNLD_LOCATION_UPLOAD_RATE.getValue(),
            OneNetConfig.ExecuteCommand.ZNLD_STATUS_UPLOAD_RATE.getValue());

    @ApiModelProperty(value = "额外的参数")
    private Integer other = null;

    public OneNetExecuteArgsEx(String cmd){
        super(cmd);
    }

    public OneNetExecuteArgsEx(String cmd, Integer other){
        super(cmd);
        this.other = other;
    }
    public boolean verify(){
        if(other != null){
            if(other <= 0 || other >= 100) return false;
            return theCommands.contains(super.args);
        }
        return true;
    }

    @ApiModelProperty(hidden = true)
    public String getArgsEx(){
        if(!verify()) return MyString.Empty;

        if(other == null) return super.args;
        var pv = other.toString();
        if(other <10 && other > 0) pv = "0"+pv;
        return super.args + pv;
    }
}
