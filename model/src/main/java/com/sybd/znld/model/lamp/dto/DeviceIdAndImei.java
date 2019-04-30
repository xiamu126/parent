package com.sybd.znld.model.lamp.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class DeviceIdAndImei implements Serializable {
    public Integer deviceId;
    public String imei;
}
