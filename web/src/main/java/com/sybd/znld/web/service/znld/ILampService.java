package com.sybd.znld.web.service.znld;

import com.sybd.znld.model.lamp.dto.CheckedResource;
import com.sybd.znld.model.lamp.dto.DeviceIdAndDeviceName;
import com.sybd.znld.model.lamp.dto.LampAndCamera;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.lamp.LampModel;
import com.sybd.znld.model.lamp.LampRegionModel;
import com.sybd.znld.model.lamp.OneNetResourceModel;

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
    LampAndCamera getActiveCameraByDeviceId(Integer deviceId);
}
