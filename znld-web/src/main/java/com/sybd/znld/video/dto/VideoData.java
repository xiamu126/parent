package com.sybd.znld.video.dto;

import com.sybd.znld.video.dto.VideoBaseData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value = "视频推送数据")
@Getter @Setter @ToString
public class VideoData extends VideoBaseData {
    @ApiModelProperty(value = "具体的推送命令，目前仅支持push，即推送")
    private String cmd;
}