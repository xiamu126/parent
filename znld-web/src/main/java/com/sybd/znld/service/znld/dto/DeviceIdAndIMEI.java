package com.sybd.znld.service.znld.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class DeviceIdAndIMEI implements Serializable {
    public Integer deviceId;
    public String imei;
}
