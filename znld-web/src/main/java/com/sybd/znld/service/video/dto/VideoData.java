package com.sybd.znld.service.video.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@ApiModel(value = "视频推送数据")
public class VideoData extends VideoBaseData {
    @ApiModelProperty(value = "具体的推送命令，目前仅支持push，即推送")
    public String cmd;
}