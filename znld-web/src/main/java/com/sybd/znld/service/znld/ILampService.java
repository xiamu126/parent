package com.sybd.znld.service.znld;

import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.znld.LampModel;
import com.sybd.znld.model.znld.LampRegionModel;
import com.sybd.znld.model.znld.OneNetResourceModel;
import com.sybd.znld.service.znld.dto.CheckedResource;
import com.sybd.znld.service.znld.dto.DeviceIdAndDeviceName;

import java.util.List;

public interface ILampService {
    boolean isDataStreamIdEnabled(OneNetKey key);
    String getImeiByDeviceId(Integer deviceId);
    List<DeviceIdAndDeviceName> getDeviceIdAndDeviceNames(String organId);
    List<CheckedResource> getCheckedResourceByDeviceId(Integer deviceId);
    OneNetResourceModel getResourceByCommandValue(String cmd);
    LampRegionModel addLampToRegion(String lampId, String regionId);
    LampRegionModel addLampToRegion(LampModel lamp, String regionId, List<String> resourceIds);
}
