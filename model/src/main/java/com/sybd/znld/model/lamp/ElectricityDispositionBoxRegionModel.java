package com.sybd.znld.model.lamp;

import com.sybd.znld.model.IValidForDbInsert;
import com.sybd.znld.util.MyString;

import java.util.UUID;

public class ElectricityDispositionBoxRegionModel implements IValidForDbInsert {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public String electricityDispositionBoxId;
    public String regionId;

    @Override
    public boolean isValidForInsert() {
        return MyString.isUuid(electricityDispositionBoxId) && MyString.isUuid(regionId);
    }
}
