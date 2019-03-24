package com.sybd.znld.service;

import com.sybd.znld.service.dto.CheckedResource;
import com.sybd.znld.service.dto.DeviceIdAndDeviceName;

import java.util.List;
import java.util.Map;

public interface OneNetConfigDeviceService extends IBaseService {
    String getImeiByDeviceId(Integer deviceId);
    String getDescBy(Integer objId, Integer objInstId, Integer resId);
    List<Integer> getDeviceIds();
    Map<Integer, String> getDeviceIdNameMap();
    String getApiKeyByDeviceId(Integer deviceId);
    String getOneNetKey(String name);
    Map<String, String> getInstanceMap(Integer deviceId);
    Map<Integer, String> getDeviceIdAndImeis();
    List<DeviceIdAndDeviceName> getDeviceIdAndDeviceNames();
    List<CheckedResource> getCheckedResources(Integer deviceId);
    boolean isDataStreamIdEnabled(String dataStreamId);
}
