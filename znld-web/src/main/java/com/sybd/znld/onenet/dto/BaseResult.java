package com.sybd.znld.onenet.dto;

public class BaseResult{
    public Integer errno;
    public String error;

    public BaseResult(){}
    public BaseResult(Integer errno, String error) {
        this.errno = errno;
        this.error = error;
    }

    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
