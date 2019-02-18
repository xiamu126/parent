package com.sybd.znld.core;

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.io.Serializable

@ApiModel(value = "统一的接口返回数据")
open class BaseApiResult constructor(@ApiModelProperty(value = "返回代码，0为成功，1为失败") var code: Int,
                                     @ApiModelProperty(value = "相关描述") var msg: String): Serializable