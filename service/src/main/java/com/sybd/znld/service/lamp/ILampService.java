package com.sybd.znld.service.lamp;

import com.sybd.znld.model.lamp.*;
import com.sybd.znld.model.lamp.dto.*;
import com.sybd.znld.model.onenet.OneNetKey;

import java.util.List;
import java.util.Map;

public interface ILampService {
    boolean isDataStreamIdEnabled(Integer deviceId, OneNetKey key);
    String getImeiByDeviceId(Integer deviceId);
    List<DeviceIdAndDeviceName> getDeviceIdAndDeviceNames(String organId);
    List<CheckedResource> getCheckedResourceByDeviceId(Integer deviceId);
    List<CheckedResource> getCheckedEnvResourceByDeviceId(Integer deviceId);
    List<CheckedResource> getCheckedResourceByOrganId(String organId);
    List<CheckedResource> getCheckedEnvResourceByOrganId(String organId);
    CheckedResource getCheckedEnvResourceByDeviceIdAndResourceDesc(Integer deviceId, String resourceDesc);
    CheckedResource getCheckedEnvResourceByOrganIdAndResourceDesc(String organId, String resourceDesc);
    OneNetResourceModel getResourceByCommandValue(String cmd);
    // 新增路灯到区域（街道）
    LampRegionModel addLampToRegion(String lampId, String regionId);
    // 新增路灯到区域（街道），并设置安装的模块；
    LampRegionModel addLampToRegionWithModules(LampModel lamp, String regionId, List<String> modules);
    LampRegionModel addLampToRegion(LampModel lamp, String regionId, List<String> resourceIds);
    List<LampModel> getLampsByRegionId(String regionId);
    LampModel getLampByDeviceId(Integer deviceId);
    LampAndCamera getActiveCameraByDeviceId(Integer deviceId);
    String getResourceNameByDataStreamId(String dataStreamId);
    String getDataStreamIdByResourceName(String resourceName);
    AppModel getAppInfoByName(String name);
    LampCameraModel addCamera(String lampId, CameraModel model);
    boolean removeCamera(String lampId, String cameraId);
    LampStatus getLampStatusByDeviceId(Integer deviceId);
    DeviceStatus getLampAngleStatusByDeviceId(Integer deviceId);
    Map<Integer, LampStatus> getLampStatusByDeviceIds(List<Integer> deviceIds);
    Map<Integer, LampStatus> getLampStatusByRegionId(String regionId);
    LampStatusResultPaged getLampStatusByRegionIdPaged(String regionId, int pageIndex, int pageSize);
    Integer getCountOfLampByRegionId(String regionId);
}
