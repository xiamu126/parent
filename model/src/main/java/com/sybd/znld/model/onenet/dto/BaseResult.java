package com.sybd.znld.model.onenet.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class BaseResult{
    public Integer errno;
    public String error;
    @JsonIgnore
    public boolean isOk(){
        return this.errno != null && this.errno == 0;
    }

    public BaseResult(){}
    public BaseResult(Integer code, String msg){
        this.errno = code;
        this.error = msg;
    }
}
