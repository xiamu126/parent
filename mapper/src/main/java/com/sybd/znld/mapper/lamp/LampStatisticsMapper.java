package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampStatisticsModel;
import com.sybd.znld.model.lamp.dto.Statistic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
@DbSource("znld")
public interface LampStatisticsMapper {
    int insert(LampStatisticsModel model);
    int update(LampStatisticsModel model);
    LampStatisticsModel selectById(String id);
    // 获取昨天的某条街上的每盏路灯的统计情况
    List<Statistic> selectLastDayLampsByRegionId(String id);
    // 获取昨天的某个分平台下的每条街的统计情况
    List<Statistic> selectLastDayRegionsByOrganId(String id);
    // 获取昨天的某个分平台的统计情况
    List<Statistic> selectLastDayByOrganId(String id);
    // 获取本周的某条街上的每盏路灯的统计情况
    List<Statistic> selectThisWeekLampsByRegionId(String id);
    // 获取本周的某个分平台的每条街的统计情况
    List<Statistic> selectThisWeekRegionsByOrganId(String id);
    // 获取本周的某个分平台的统计情况
    List<Statistic> selectThisWeekByOrganId(String id);
    // 获取本月的某个条街下的每盏路灯的统计情况
    List<Statistic> selectThisMonthLampsByRegionId(String id);
    // 获取本月的某个分平台的每条街的统计情况
    List<Statistic> selectThisMonthRegionsByOrganId(String id);
    // 获取本月的某个分平台的统计情况
    List<Statistic> selectThisMonthByOrganId(String id);
    // 获取今年的某个街道下的每盏路灯的统计情况
    List<Statistic> selectThisYearLampsByRegionId(String id);
    // 获取今年的某个分平台的每条街的统计情况
    List<Statistic> selectThisYearRegionsByOrganId(String id);
    // 获取今年的某个分平台的统计情况
    List<Statistic> selectThisYearByOrganId(String id);
    // 获取某条街上的每盏路灯的统计情况，指定时间区间
    List<Statistic> selectLampsByRegionIdBetween(@Param("id") String id, @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
    // 获取某个分平台的每条街的统计情况，指定时间区间
    List<Statistic> selectRegionsByOrganIdBetween(@Param("id") String id, @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
    // 获取某个分平台的统计情况，指定时间区间
    List<Statistic> selectByOrganIdBetween(@Param("id") String id, @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
    // 获取本周的某个分平台的统计情况，按天分组
    List<Statistic> selectThisWeekGroupDayByOrganId(String id);
    // 获取本月的某个分平台的统计情况，按天分组
    List<Statistic> selectThisMonthGroupDayByOrganId(String id);
    // 获取今年的某个分平台的统计情况，按月分组
    List<Statistic> selectThisYearGroupDayByOrganId(String id);
}
