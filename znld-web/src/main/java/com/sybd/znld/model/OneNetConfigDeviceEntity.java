package com.sybd.znld.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OneNetConfigDeviceEntity implements Serializable {
    private Integer id;
    private String apiKey;
    private String deviceId;
    private String imei;
    private Integer objId;
    private Integer objInstId;
    private Integer resId;
    private String name;
    private String description;
    private Integer timeout;
    private String longitude;
    private String latitude;
    private String deviceName;
    private Boolean checked;
}
