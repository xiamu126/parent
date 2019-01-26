package com.sybd.znld.controller.device.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.onenet.dto.GetDataStreamByIdResult
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.modelmapper.ModelMapper
import org.modelmapper.PropertyMap

import java.time.LocalDateTime;

@ApiModel(value = "获取设备当前最新数据的返回值")
class LastDataResult(code: Int, msg: String): BaseApiResult(code, msg) {
    @ApiModelProperty(value = "最后的更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "CN")
    private var updateAt: LocalDateTime? = null
    @ApiModelProperty(value = "当前的数值")
    private var currentValue: String = ""

    companion object {
        @JvmStatic fun fail(msg: String): LastDataResult{
            return LastDataResult(1, msg)
        }

        @JvmStatic fun success(result: GetDataStreamByIdResult): LastDataResult{
            val propertyMap = object: PropertyMap<GetDataStreamByIdResult, LastDataResult>() {
                override fun configure() {
                    map(source.data!!.updateAt, destination.updateAt);
                    map(source.data!!.currentValue, destination.currentValue);
                    skip(destination.code);
                    skip(destination.msg);
                }
            };
            val modelMapper = ModelMapper()
            modelMapper.addMappings(propertyMap);
            modelMapper.validate();
            val tmp = modelMapper.map(result, LastDataResult::class.java);
            tmp.code = 0;
            return tmp;
        }
    }


}
