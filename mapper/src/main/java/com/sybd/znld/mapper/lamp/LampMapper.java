package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.dto.*;
import com.sybd.znld.model.lamp.LampModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
@DbSource("znld")
public interface LampMapper {
    int insert(LampModel model);
    LampModel selectById(String lampId);
    LampModel selectByDeviceId(Integer deviceId);
    LampModel selectByDeviceName(String deviceName);
    LampModel selectByLatitude(String latitude);
    LampModel selectByLongitude(String longitude);
    List<Integer> selectAllDeviceIds();
    String selectApiKeyByDeviceId(Integer deviceId);
    HashMap<String, String> selectResourceMapByDeviceId(Integer deviceId);
    String selectImeiByDeviceId(Integer deviceId);
    List<DeviceIdAndImei> selectAllDeviceIdAndImei();
    List<DeviceIdAndDeviceName> selectDeviceIdAndDeviceNames(String organId);
    List<CheckedResource> selectCheckedResourceByDeviceId(Integer deviceId);
    List<CheckedResource> selectCheckedEnvResourceByDeviceId(Integer deviceId);
    CheckedResource selectCheckedResourceByDeviceIdAndResourceDesc(Integer deviceId, String resourceDesc);
    CheckedResource selectCheckedEnvResourceByDeviceIdAndResourceDesc(Integer deviceId, String resourceDesc);
    List<LampAndRegion> selectLampByDeviceIdAndRegionType(Integer deviceId, Short regionType);
    List<LampAndRegion> selectLampByLampIdAndRegionType(String lampId, Short regionType);
    List<BoundResource> selectBoundResourceByDeviceId(Integer deviceId);
    List<LampModel> selectByRegionId(String regionId);
    List<LampModel> selectByOrganId(String organId, int limit, int offset);
    LampAndCamera selectActiveCameraByDeviceId(Integer deviceId);
}
