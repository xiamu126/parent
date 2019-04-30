package com.sybd.znld.model.lamp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class LampCameraModel implements Serializable {
    public String id;
    public String lampId;
    public String cameraId;
}
