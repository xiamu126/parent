package com.sybd.znld.controller.user.dto;

import com.sybd.znld.core.BaseApiResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value = "登入返回数据")
class LoginResult constructor(@ApiModelProperty(value = "登入后返回的用户Id")
                              val userId: String,
                              @ApiModelProperty(value = "获取OAuth2的客户Id")
                              val clientId: String,
                              @ApiModelProperty(value = "获取OAuth2的客户Secret")
                              val clientSecret: String,
                              @ApiModelProperty(value = "OAuth2的过期时间")
                              val auth2TokenExpirationTime: Long,
                              code: Int, msg: String): BaseApiResult(code, msg) {
    companion object {
        @JvmStatic fun fail(msg: String): LoginResult{
            return LoginResult("", "","", 0, 1, msg)
        }
        @JvmStatic fun success(userId:String, clientId:String, clientSecret:String, auth2TokenExpirationTime:Long): LoginResult{
            return LoginResult(userId, clientId, clientSecret, auth2TokenExpirationTime, 0, "")
        }
    }
}
