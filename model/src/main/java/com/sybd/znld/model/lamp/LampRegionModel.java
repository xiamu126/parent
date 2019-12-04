package com.sybd.znld.model.lamp;

import java.io.Serializable;
import java.util.UUID;

public class LampRegionModel implements Serializable {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public String lampId;
    public String regionId;
}
