package com.sybd.znld.light.service;

import com.sybd.znld.light.controller.dto.RegionBoxLamp;

public interface IDeviceService {
    // 获取某个区域下的所有路灯
    RegionBoxLamp getLamps(String organId, String regionId);
    // 把某个区域下的路灯管理到某个配电箱下面
    boolean addLampsOfRegionToBox(String regionId, String boxId);
}
