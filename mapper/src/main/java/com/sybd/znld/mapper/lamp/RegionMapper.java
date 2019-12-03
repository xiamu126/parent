package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.Status;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxModel;
import com.sybd.znld.model.lamp.LampModel;
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
    RegionModel selectByRegionIdAndOrganId(@Param("regionId") String regionId, String organId);
    List<RegionModel> selectAll();
    RegionModel selectOne();
    List<RegionModel> select(int count);
    int updateById(RegionModel model);

    List<RegionWithLocation> selectAllRegionWithValidLamp(String organId);
    List<Region> selectByOrganId(String organId);
    List<LampModel> selectLampsByRegionId(String regionId);
    List<ElectricityDispositionBoxModel> selectBoxesByRegionId(String id);
    List<LampWithLocation> selectLampsWithLocationByRegionId(String regionId);
    List<Lamp> selectLampsOfEnvironment(String organId);
    // 获取某个组织的某个区域下的所有（正常运行）路灯
    List<Lamp> selectLampsByOrganIdRegionIdNotStatus(@Param("organId") String organId, @Param("regionId") String regionId, @Param("status") Status status);
    List<LampModel> selectLampsByOrganIdRegionId(@Param("organId") String organId, @Param("regionId") String regionId);
    // 获取某个区域下的所有配电箱
    List<ElectricityDispositionBoxModel> selectBoxesByOrganIdRegionId(@Param("organId") String organId, @Param("regionId") String regionId);
    // 某个时间区间内的平均值
    List<ElementAvgResult> selectAvgOfEnvironmentElementBetweenByDeviceId(@Param("deviceId") Integer deviceId, @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
    List<ElementAvgResult> selectAvgOfEnvironmentElementLastHoursWithBeginTimeByDeviceId(@Param("deviceId") Integer deviceId, @Param("begin") LocalDateTime begin, @Param("hours") Integer hours);
    // 昨天的平均值
    List<ElementAvgResult> selectAvgOfEnvironmentElementLastDayByDeviceId(Integer deviceId);
    // 昨天过去N小时的平均值（即昨天23:59:59为起点往后推N个小时），N不能超过24
    List<ElementAvgResult> selectAvgOfEnvironmentElementLastDayLastHoursByDeviceId(@Param("deviceId") Integer deviceId, @Param("hours") Integer hours);
    // 过去1小时平均值
    List<ElementAvgResult> selectAvgOfEnvironmentElementLastHourByDeviceId(Integer deviceId);
    // 过去N个小时的平均值
    List<ElementAvgResult> selectAvgOfEnvironmentElementLastHoursByDeviceId(@Param("deviceId") Integer deviceId, @Param("hours") Integer hours);
    // 获取每个小时的平均值，指定结束时间
    List<ElementAvgResult> selectAvgOfEnvironmentElementHourlyWithEndTimeByDeviceId(@Param("deviceId") Integer deviceId, @Param("name") String name, @Param("end") LocalDateTime end);
    List<ElementAvgResult> selectAvgOfEnvironmentElementHourlyByDeviceId(@Param("deviceId") Integer deviceId, @Param("name") String name);
    List<ElementAvgResult> selectAvgOfEnvironmentElementDailyByDeviceId(@Param("deviceId") Integer deviceId, @Param("name") String name);
    List<ElementAvgResult> selectAvgOfEnvironmentElementMonthlyByDeviceId(@Param("deviceId") Integer deviceId, @Param("name") String name);
    List<ElementAvgResult> selectAvgOfEnvironmentElementHourlyByDeviceIdBetween(@Param("deviceId") Integer deviceId, @Param("name") String name,
                                                                                @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
    List<ElementAvgResult> selectAvgOfEnvironmentElementDailyByDeviceIdBetween(@Param("deviceId") Integer deviceId, @Param("name") String name,
                                                                               @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
    List<ElementAvgResult> selectAvgOfEnvironmentElementMonthlyByDeviceIdBetween(@Param("deviceId") Integer deviceId, @Param("name") String name,
                                                                                 @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
    Object selectByRegionIdAndUserId(@Param("regionId") String regionId, @Param("userId") String userId);
}
