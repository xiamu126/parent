package com.sybd.znld.video.dto;

import com.sybd.znld.video.dto.VideoBaseData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "视频推送数据")
public class VideoData extends VideoBaseData {
    @ApiModelProperty(value = "具体的推送命令，目前仅支持push，即推送")
    public String cmd;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}