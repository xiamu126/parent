package com.sybd.znld.light.service;

import com.sybd.znld.light.controller.dto.RegionBoxLamp;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxModel;
import com.sybd.znld.model.lamp.LampModel;

import java.util.List;

public interface IDeviceService {
    // 获取某个区域下的所有路灯
    RegionBoxLamp getLamps(String organId, String regionId);
    // 获取某个组织下的所有路灯
    List<RegionBoxLamp> getLamps(String organId);
    // 新增路灯并添加到区域
    boolean insertAndAddLampsToRegion(List<LampModel> lamps, String regionId);
    // 新增配电箱并添加到区域
    boolean insertAndAddBoxesToRegion(List<ElectricityDispositionBoxModel> boxes, String regionId);
    // 把路灯添加到某个区域
    boolean addLampsToRegion(List<String> ids, String regionId);
    // 把配电箱添加到某个区域
    boolean addBoxesToRegion(List<String> ids, String regionId);
    // 把某个区域下的路灯关联到某个配电箱下面
    boolean addLampsOfRegionToBox(String regionId, String boxId);
}
