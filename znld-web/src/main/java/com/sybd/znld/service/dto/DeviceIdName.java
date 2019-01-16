package com.sybd.znld.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class DeviceIdName implements Serializable {
    private Integer id;
    private String name;
}
