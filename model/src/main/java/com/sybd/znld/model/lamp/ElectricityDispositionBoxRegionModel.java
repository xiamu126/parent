package com.sybd.znld.model.lamp;

import java.util.UUID;

public class ElectricityDispositionBoxRegionModel {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public String electricityDispositionBoxId;
    public String regionId;
}
