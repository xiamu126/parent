package com.sybd.znld.model.znld;

import java.io.Serializable;

public class ExecuteCommandModel implements Serializable {
    public Integer id;
    public Integer objId;
    public Integer objInstId;
    public Integer resId;
    public String value;
    public String description;
    public Short timeout;
}
