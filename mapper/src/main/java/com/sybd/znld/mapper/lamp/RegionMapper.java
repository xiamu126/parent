package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.RegionModel;
import com.sybd.znld.model.lamp.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
@DbSource("znld")
public interface RegionMapper {
    int insert(RegionModel model);
    RegionModel selectById(String id);
    RegionModel selectByName(String name);
    List<RegionModel> selectAll();
    RegionModel selectOne();
    List<RegionModel> select(int count);
    int updateById(RegionModel model);
    List<RegionWithLocation> selectAllRegionWithValidLamp(String organId);
    List<Region> selectByOrganId(String organId);
    List<Lamp> selectLampsByRegionId(String regionId);
    List<Lamp> selectLampsOfEnvironment(String organId);
    List<ElementAvgResult> selectAvgOfEnvironmentElementByDeviceId(@Param("deviceId") Integer deviceId, @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
    List<ElementAvgResult> selectAvgOfEnvironmentElementLastDayByDeviceId(Integer deviceId);
    List<ElementAvgResult> selectAvgOfEnvironmentElementLastHourByDeviceId(Integer deviceId);
    List<ElementAvgResult> selectAvgOfEnvironmentElementHourlyByDeviceId(@Param("deviceId") Integer deviceId, @Param("name") String name);
    List<ElementAvgResult> selectAvgOfEnvironmentElementDailyByDeviceId(@Param("deviceId") Integer deviceId, @Param("name") String name);
    List<ElementAvgResult> selectAvgOfEnvironmentElementMonthlyByDeviceId(@Param("deviceId") Integer deviceId, @Param("name") String name);
    List<ElementAvgResult> selectAvgOfEnvironmentElementHourlyByDeviceIdBetween(@Param("deviceId") Integer deviceId, @Param("name") String name,
                                                                                @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
    List<ElementAvgResult> selectAvgOfEnvironmentElementDailyByDeviceIdBetween(@Param("deviceId") Integer deviceId, @Param("name") String name,
                                                                               @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
    List<ElementAvgResult> selectAvgOfEnvironmentElementMonthlyByDeviceIdBetween(@Param("deviceId") Integer deviceId, @Param("name") String name,
                                                                                 @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
}
