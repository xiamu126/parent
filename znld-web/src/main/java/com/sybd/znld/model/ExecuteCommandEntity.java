package com.sybd.znld.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ExecuteCommandEntity implements Serializable {
    private Integer id;
    private Integer objId;
    private Integer objInstId;
    private Integer resId;
    private String value;
    private String description;
    private Integer timeout;
}
