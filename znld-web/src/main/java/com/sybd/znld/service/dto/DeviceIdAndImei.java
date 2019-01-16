package com.sybd.znld.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class DeviceIdAndImei implements Serializable {
    private Integer deviceId;
    private String imei;
}
