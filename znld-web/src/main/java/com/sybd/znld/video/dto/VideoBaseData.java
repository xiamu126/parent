package com.sybd.znld.video.dto;

import io.swagger.annotations.ApiModelProperty;

public class VideoBaseData {
    @ApiModelProperty(value = "推送的频道，每个摄像头的都会绑定一个唯一的频道")
    public String channelGuid;

    public String getChannelGuid() {
        return channelGuid;
    }

    public void setChannelGuid(String channelGuid) {
        this.channelGuid = channelGuid;
    }
}
