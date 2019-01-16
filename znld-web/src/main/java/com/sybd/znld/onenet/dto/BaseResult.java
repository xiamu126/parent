package com.sybd.znld.onenet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaseResult {
    protected Integer errno;
    protected String error;
}
