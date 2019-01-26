package com.sybd.znld.controller.device.dto;

import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.service.dto.CheckedResource;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "获取选中资源的返回值")
class CheckedResourcesResult(code: Int, msg: String): BaseApiResult(code, msg) {
    private var checkedResources: List<CheckedResource>? = null

    companion object {
        @JvmStatic fun fail(msg: String): CheckedResourcesResult{
            return CheckedResourcesResult(1, msg)
        }
        @JvmStatic fun success(result: List<CheckedResource>): CheckedResourcesResult{
            val tmp = CheckedResourcesResult(0, "")
            tmp.checkedResources = result
            return tmp
        }
    }
}
