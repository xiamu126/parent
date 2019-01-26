package com.sybd.znld.onenet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

class GetDataStreamsByIdsResult(val data: Array<Data>?, errno: Int, error: String): BaseResult(errno, error) {
    fun isOk(): Boolean{
        return errno == 0 && data != null && data.isNotEmpty();
    }
    inner class Data constructor(@JsonProperty("update_at")
                                 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "CN")
                                 val  updateAt: LocalDateTime,
                                 val id: String,
                                 @JsonProperty("create_time")
                                 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
                                 val createTime: LocalDateTime,
                                 @JsonProperty("current_value")
                                 val currentValue: Any)
}
