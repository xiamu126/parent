package com.sybd.znld.onenet.dto;

import com.sybd.znld.onenet.OneNetConfig;
import com.whatever.util.MyString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Arrays;

@ApiModel(value = "命令执行参数")
class OneNetExecuteArgsEx(args: String): OneNetExecuteArgs(args) {
    @ApiModelProperty(hidden = true)
    private var theCommands = listOf(OneNetConfig.ExecuteCommand.ZNLD_QX_UPLOAD_RATE.value,
            OneNetConfig.ExecuteCommand.ZNLD_LOCATION_UPLOAD_RATE.value,
            OneNetConfig.ExecuteCommand.ZNLD_STATUS_UPLOAD_RATE.value)

    @ApiModelProperty(value = "额外的参数")
    private var other: Int? = null

    constructor(cmd: String , other: Int?): this(cmd){
        this.other = other
    }

    fun verify(): Boolean {
        other?.let {
            if(it <= 0 || it >= 100) return false
            return this.args in theCommands
        }
        return true
    }

    @ApiModelProperty(hidden = true)
    fun getArgsEx(): String{
        if(!verify()) return ""
        other?.let {
            var pv = it.toString()
            if(it in 1..9) pv = "0$pv"
            return this.args + pv
        }
        return super.args
    }
}