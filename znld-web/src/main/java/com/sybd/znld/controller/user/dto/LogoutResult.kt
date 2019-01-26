package com.sybd.znld.controller.user.dto;

import com.sybd.znld.core.BaseApiResult;
import io.swagger.annotations.ApiModel;


@ApiModel(value = "登出返回数据")
class LogoutResult(code: Int, msg: String): BaseApiResult(code, msg) {
    companion object {
        @JvmStatic fun fail(msg: String): LogoutResult{
            return LogoutResult(1, msg)
        }
        @JvmStatic fun success(): LogoutResult{
            return LogoutResult(0, "")
        }
    }
}
