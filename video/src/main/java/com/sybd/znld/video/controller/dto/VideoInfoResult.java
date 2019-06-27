package com.sybd.znld.video.controller.dto;

import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.dto.LampAndCamera;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(value = "获取视频相关数据")
@NoArgsConstructor
@Getter
@Setter
public class VideoInfoResult extends BaseApiResult {
    @ApiModelProperty(value = "摄像头id")
    public String cameraId;
    @ApiModelProperty(value = "视频地址")
    public String flvUrl;

    public VideoInfoResult(int code, String msg){
        super(code, msg);
    }

    public static VideoInfoResult fail(String msg){
        return new VideoInfoResult(1, msg);
    }

    public static VideoInfoResult success(LampAndCamera data){
        var videoInfoResult = new VideoInfoResult();
        videoInfoResult.cameraId = data.cameraId;
        videoInfoResult.flvUrl = data.flvUrl;
        videoInfoResult.code = 0;
        videoInfoResult.msg = "";
        return videoInfoResult;
    }
}
