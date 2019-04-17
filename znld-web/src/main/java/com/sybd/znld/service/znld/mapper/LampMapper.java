package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.LampModel;
import com.sybd.znld.model.dto.DeviceIdAndImei;
import com.sybd.znld.service.znld.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    List<LampAndRegion> selectLampByDeviceIdAndRegionType(Integer deviceId, Short regionType);
    List<LampAndRegion> selectLampByLampIdAndRegionType(String lampId, Short regionType);
    List<BoundResource> selectBoundResourceByDeviceId(Integer deviceId);
    List<LampModel> selectByRegionId(String regionId);
}
