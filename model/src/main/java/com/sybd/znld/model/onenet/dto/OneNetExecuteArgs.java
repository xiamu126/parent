package com.sybd.znld.model.onenet.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.model.IValid;
import com.sybd.znld.model.ministar.dto.Subtitle;
import com.sybd.znld.model.onenet.Command;
import com.sybd.znld.util.MyNumber;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

// 这个类为接受前端传来的数据
@Slf4j
@ApiModel(value = "命令执行参数")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OneNetExecuteArgs implements IValid {
    @ApiModelProperty(value = "具体的命令")
    public String cmd;
    @ApiModelProperty(value = "额外的数据，如果额外的数据为复杂数据、则这应该是一个json字符串，相反如果为简单数据，则这个值即本身，如在修改频率的时候，这个值即具体的频率值")
    public String json;

    @Override
     public boolean isValid(){
        if(this.cmd == null) return false;
        if(this.cmd.equals(Command.ZNLD_QX_UPLOAD_RATE) || this.cmd.equals(Command.ZNLD_LOCATION_UPLOAD_RATE) || this.cmd.equals(Command.ZNLD_STATUS_UPLOAD_RATE)){
            // 检查具体的速率，必须是[10,100)
            return MyNumber.isBetween(this.json, 10, 99);
        } else if(this.cmd.equals(Command.ZNLD_DD_EXECUTE)){
            // 如果是灯带的，检查指令是否合法
            if(this.json == null) return false;
            var subtitle = this.getSubtitle();
            return subtitle != null && subtitle.isValid();
        }
        return !cmd.equals("");
    }

    public String getPackedCmd(){
        if(!this.isValid()) return "";
        if(this.cmd.equals(Command.ZNLD_QX_UPLOAD_RATE) || this.cmd.equals(Command.ZNLD_LOCATION_UPLOAD_RATE) || this.cmd.equals(Command.ZNLD_STATUS_UPLOAD_RATE)){
            // 检查具体的速率，必须是[10,100)
            return this.cmd = this.cmd + this.json;
        } else if(this.cmd.equals(Command.ZNLD_DD_EXECUTE)){
            // 如果是灯带的，检查指令是否合法
            var subtitle = this.getSubtitle();
            if(subtitle == null) return "";
            return subtitle.toString();
        }
        return cmd;
    }

    public Subtitle getSubtitle(){
        var objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(this.json, Subtitle.class);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return null;
    }
}
