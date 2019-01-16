package com.sybd.znld.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DeviceDataEntity implements Serializable {
    private String id;
    private String at;
    private String theId;
    private String theValue;
}
