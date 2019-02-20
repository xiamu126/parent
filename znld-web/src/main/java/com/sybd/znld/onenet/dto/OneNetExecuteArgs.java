package com.sybd.znld.onenet.dto;

import io.swagger.annotations.ApiModelProperty;

public class OneNetExecuteArgs{
    @ApiModelProperty(value = "具体的命令")
    public String args = "SYBD";

    public OneNetExecuteArgs(String args) {
        this.args = args;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
}
