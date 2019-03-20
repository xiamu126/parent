package com.sybd.znld.service.znld;

import com.sybd.znld.service.model.znld.RegionModel;

public interface IRegionService {
    RegionModel addRegion(RegionModel model);
    RegionModel getRegionById(String id);
    RegionModel getRegionByName(String name);
    RegionModel modifyRegionById(RegionModel model);
}
