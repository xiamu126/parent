package com.sybd.znld.onenet.dto;

import com.sybd.znld.onenet.OneNetService;
import com.sybd.znld.service.ministar.dto.Subtitle;
import com.sybd.znld.util.MyByte;
import com.sybd.znld.util.MyNumber;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

// 这个类为接受前端传来的数据
@ApiModel(value = "命令执行参数")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OneNetExecuteArgs{
    @ApiModelProperty(value = "具体的命令")
    public String cmd;
    @ApiModelProperty(value = "额外的数据，如果额外的数据为复杂数据、则这应该是一个json字符串，相反如果为简单数据，则这个值即本身，如在修改频率的时候，这个值即具体的频率值")
    public String json;

     public boolean isValid(){
        if(this.cmd.equals(OneNetService.ZNLD_QX_UPLOAD_RATE) || this.cmd.equals(OneNetService.ZNLD_LOCATION_UPLOAD_RATE) || this.cmd.equals(OneNetService.ZNLD_STATUS_UPLOAD_RATE)){
            // 检查具体的速率，必须是[10,100)
            return MyNumber.isBetween(this.json, 10, 99);
        }
        if(this.cmd.equals(OneNetService.ZNLD_DD_EXECUTE)){
            // 如果是灯带的，检查指令是否合法
            if(this.json == null) return false;
            var modelMapper = new ModelMapper();
            var subtitle = modelMapper.map(this.json, Subtitle.class);
            return subtitle != null && subtitle.isValid();
        }
        return true;
    }
}
