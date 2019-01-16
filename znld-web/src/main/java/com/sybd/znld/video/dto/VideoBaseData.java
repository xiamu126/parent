package com.sybd.znld.video.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
public class VideoBaseData {
    @ApiModelProperty(value = "推送的频道，每个摄像头的都会绑定一个唯一的频道")
    private String channelGuid;
}
