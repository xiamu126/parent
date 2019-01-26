package com.sybd.znld.onenet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

class GetLastDataStreamsResult(val data: Data, errno: Int, error: String): BaseResult(errno, error) {

    fun isOk(): Boolean{
        return errno == 0 && data.devices.isNotEmpty();
    }

    inner class Data(val devices: Array<Device>)
    inner class Device(val title: String, val id: String, val datastreams: Array<DataStream>)
    inner class DataStream(val at: String, val id: String, var value:Any)
}
