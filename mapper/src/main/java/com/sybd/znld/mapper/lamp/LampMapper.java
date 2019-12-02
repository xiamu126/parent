package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.RegionModel;
import com.sybd.znld.model.lamp.dto.*;
import com.sybd.znld.model.lamp.LampModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

@Mapper
@DbSource("znld")
public interface LampMapper {
    int insert(LampModel model);
    List<LampSummary> selectLampSummary();
    List<LampModel> selectEnvironmentLampByOrganId(String organId);
    LampModel selectById(String lampId);
    LampModel selectByDeviceId(Integer deviceId);
    LampModel selectByImei(String imei);
    LampModel selectByDeviceName(String deviceName);
    LampModel selectByLatitude(String latitude);
    LampModel selectByLongitude(String longitude);
    Location selectLocationByDeviceId(Integer deviceId);
    List<Integer> selectAllDeviceIds();
    String selectApiKeyByDeviceId(Integer deviceId);
    String selectApiKeyByImei(String imei);
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
    List<LampModel> selectByRegionIdPaged(@Param("regionId") String regionId, @Param("pageIndex") int pageIndex, @Param("pageSize") int pageSize);
    Integer selectCountOfLampByRegionId(String regionId);
    List<LampModel> selectByOrganId(@Param("organId") String organId, @Param("limit") int limit, @Param("offset") int offset);
    LampAndCamera selectActiveCameraByDeviceId(Integer deviceId);
    LampAndCamera selectActiveCameraByLampId(String lampId);
    RegionModel selectRegionByLampId(String lampId);
}
