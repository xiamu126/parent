package com.sybd.znld.model.onenet.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

// 这个类为发送给OneNet的数据
@Getter @Setter
public class OneNetExecuteParams implements Serializable {
    public String args;
}
