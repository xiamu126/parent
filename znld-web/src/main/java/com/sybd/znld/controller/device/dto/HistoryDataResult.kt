package com.sybd.znld.controller.device.dto;

import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.onenet.dto.GetHistoryDataStreamResult
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.modelmapper.ModelMapper
import org.modelmapper.PropertyMap

@ApiModel(value = "获取设备历史数据的返回值")
class HistoryDataResult(code: Int, msg: String): BaseApiResult(code, msg) {
    @ApiModelProperty(value = "当前的游标位置")
    private var cursor: String = ""
    @ApiModelProperty(value = "具体的历史数据")
    private var dataStreams: Array<DataStream>? = null

    inner class DataStream(var at: String, var value: String)

    companion object {
        @JvmStatic fun fail(msg: String): HistoryDataResult{
            return HistoryDataResult(1, msg)
        }

        @JvmStatic fun success(result: GetHistoryDataStreamResult): HistoryDataResult{
            val propertyMap = object: PropertyMap<GetHistoryDataStreamResult, HistoryDataResult>() {
                override fun configure(){
                    map(source.data!!.cursor, destination.cursor);
                    map(source.data!!.datastreams, destination.dataStreams);
                    skip(destination.code);
                    skip(destination.msg);
                }
            };
            val modelMapper = ModelMapper();
            modelMapper.addMappings(propertyMap);
            modelMapper.validate();
            val tmp = modelMapper.map(result, HistoryDataResult::class.java)
            tmp.code = 0;
            return tmp;
        }
    }
}
