package com.sybd.znld.web.controller.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.BaseApiResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "APP相关配置文件返回数据")
public class GetAppProfileResult extends BaseApiResult {
    @ApiModelProperty(value = "API接口地址")
    @JsonProperty("api_url")
    public String apiUrl;
    @ApiModelProperty(value = "APP名字")
    @JsonProperty("app_name")
    public String appName;
}
