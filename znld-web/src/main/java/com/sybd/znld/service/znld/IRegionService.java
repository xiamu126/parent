package com.sybd.znld.service.znld;

import com.sybd.znld.model.RegionModel;

import java.util.List;

public interface IRegionService {
    RegionModel getOneRegion();
    RegionModel addRegion(RegionModel model);
    RegionModel getRegionById(String id);
    RegionModel getRegionByName(String name);
    RegionModel modifyRegionById(RegionModel model);
    List<RegionModel> getAllRegion();
    List<RegionModel> getRegion(int count);
}
