package com.sybd.znld.onenet.dto;

import java.io.Serializable;

public class OneNetExecuteParams implements Serializable {
    public OneNetKey oneNetKey;
    public int timeout;

    public OneNetExecuteParams(OneNetKey oneNetKey, int timeout) {
        this.oneNetKey = oneNetKey;
        this.timeout = timeout;
    }

    public OneNetKey getOneNetKey() {
        return oneNetKey;
    }

    public void setOneNetKey(OneNetKey oneNetKey) {
        this.oneNetKey = oneNetKey;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
