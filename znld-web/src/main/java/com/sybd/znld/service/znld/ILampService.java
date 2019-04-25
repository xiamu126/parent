package com.sybd.znld.service.znld;

import com.sybd.onenet.model.OneNetKey;
import com.sybd.znld.model.LampModel;
import com.sybd.znld.model.LampRegionModel;
import com.sybd.znld.model.OneNetResourceModel;
import com.sybd.znld.service.znld.dto.CheckedResource;
import com.sybd.znld.service.znld.dto.DeviceIdAndDeviceName;

import java.util.List;

public interface ILampService {
    boolean isDataStreamIdEnabled(Integer deviceId, OneNetKey key);
    String getImeiByDeviceId(Integer deviceId);
    List<DeviceIdAndDeviceName> getDeviceIdAndDeviceNames(String organId);
    List<CheckedResource> getCheckedResourceByDeviceId(Integer deviceId);
    List<CheckedResource> getCheckedEnvResourceByDeviceId(Integer deviceId);
    List<CheckedResource> getCheckedResourceByOrganId(String organId);
    List<CheckedResource> getCheckedEnvResourceByOrganId(String organId);
    OneNetResourceModel getResourceByCommandValue(String cmd);
    LampRegionModel addLampToRegion(String lampId, String regionId);
    LampRegionModel addLampToRegion(LampModel lamp, String regionId, List<String> resourceIds);
    List<LampModel> getLampsByRegionId(String regionId);
}
