package com.sybd.znld.light.service;

import com.sybd.znld.light.controller.dto.OperationParams;
import com.sybd.znld.light.controller.dto.RegionBoxLamp;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxModel;
import com.sybd.znld.model.lamp.LampModel;
import com.sybd.znld.model.lamp.dto.OpResult;
import com.sybd.znld.model.onenet.dto.BaseResult;

import java.util.List;
import java.util.Map;

public interface IDeviceService {
    // 打开或关闭某个设备的电源（继电器）
    Map<String, BaseResult> operateDevice(OperationParams operationParams);
    // 查询设备开关(电源)状态
    /**
     * @param imeis  设备的imei，支持多个设备
     * @param names  模块名称，例如电子屏开关，支持多个模块
     */
    Map<String, Map<String, String>> getDevicePowerStatus(List<String> imeis, List<String> names);
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
