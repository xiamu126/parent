package com.sybd.znld.controller.device.dto;

import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.onenet.dto.OneNetExecuteResult
import io.swagger.annotations.ApiModel;

@ApiModel(value = "执行命令后的返回值")
class ExecuteResult(code: Int, msg: String): BaseApiResult(code, msg) {
    companion object {
        @JvmStatic fun fail(msg: String): ExecuteResult{
            return ExecuteResult(1, msg)
        }
        @JvmStatic fun success(result: OneNetExecuteResult): ExecuteResult{
            return ExecuteResult(0, "")
        }
    }
}
