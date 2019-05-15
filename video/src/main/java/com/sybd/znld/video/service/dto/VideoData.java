package com.sybd.znld.video.service.dto;

import com.sybd.znld.model.IValid;
import com.sybd.znld.util.MyDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter @Setter
@ApiModel(value = "视频推送数据")
public class VideoData implements IValid {
    @ApiModelProperty(value = "推送的频道，每个摄像头的都会绑定一个唯一的频道")
    public String channelGuid;
    @ApiModelProperty(value = "具体的推送命令，目前仅支持push，即推送")
    public String cmd;
    @ApiModelProperty(value = "若为push命令，则需指定类型，live或track")
    public String type = "live";
    @ApiModelProperty(value = "若为push命令，且为track类型，则需指定开始时间")
    public LocalDateTime startTime;

    @Override
    public boolean isValid(){
        if(!cmd.equals("push")) return false;
        if(!types.contains(type)) return false;
        if(type.equals("track")) {
            if(startTime == null) return false;
            return MyDateTime.isPast(startTime);
        }
        return true;
    }
    private static final List<String> types = Arrays.asList("live", "track");
}