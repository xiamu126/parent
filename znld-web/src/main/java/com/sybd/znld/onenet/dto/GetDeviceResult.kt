package com.sybd.znld.onenet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

class GetDeviceResult(val data: Data?, errno: Int, error: String): BaseResult(errno, error) {

    fun isOk(): Boolean{
        return errno == 0 && data != null && data.dataStreams.isNotEmpty();
    }

    inner class Data constructor(@JsonProperty("private")
                                 val thePrivate: Boolean,
                                 @JsonProperty("create_time")
                                 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
                                 val createTime: LocalDateTime,
                                 val title: String, val protocol: String, val online: Boolean, val id: Int, val imsi: String,
                                 @JsonProperty("datastreams")
                                 val dataStreams: Array<DataStream>)
    inner class DataStream constructor( @JsonProperty("create_time")
                                        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
                                        val createTime: LocalDateTime, val uuid: String, val id: String)
}
