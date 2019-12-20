package com.sybd.znld.light.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.light.controller.dto.LampAlarmOutput;
import com.sybd.znld.light.service.dto.Report;
import com.sybd.znld.light.websocket.handler.LampStatisticsHandler;
import com.sybd.znld.mapper.lamp.LampAlarmMapper;
import com.sybd.znld.mapper.lamp.LampMapper;
import com.sybd.znld.mapper.lamp.LampStatisticsMapper;
import com.sybd.znld.model.environment.RawData;
import com.sybd.znld.model.environment.RealTimeData;
import com.sybd.znld.model.lamp.LampStatisticsModel;
import com.sybd.znld.model.lamp.dto.LampStatistic;
import com.sybd.znld.model.lamp.dto.LampStatistics;
import com.sybd.znld.model.onenet.Config;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportService implements IReportService {
    private final LampStatisticsMapper lampStatisticsMapper;
    private final LampAlarmMapper lampAlarmMapper;
    private final RedissonClient redissonClient;
    private final IOneNetService oneNetService;
    private final ObjectMapper objectMapper;
    private final LampMapper lampMapper;

    @Autowired
    public ReportService(LampStatisticsMapper lampStatisticsMapper,
                         LampAlarmMapper lampAlarmMapper,
                         RedissonClient redissonClient,
                         IOneNetService oneNetService,
                         ObjectMapper objectMapper,
                         LampMapper lampMapper) {
        this.lampStatisticsMapper = lampStatisticsMapper;
        this.lampAlarmMapper = lampAlarmMapper;
        this.redissonClient = redissonClient;
        this.oneNetService = oneNetService;
        this.objectMapper = objectMapper;
        this.lampMapper = lampMapper;
    }

    @Override
    public Report getReport(String organId, Report.TimeType type) {
        var obj = new Report();
        obj.organId = organId;
        if(type == Report.TimeType.WEEK) {
            // 查看本周的统计
            var data = this.lampStatisticsMapper.selectThisWeekGroupDayByOrganId(organId);
            if(data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.electricity;
                    detail.fullElectricity = d.electricity;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d->d.electricity).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.electricity).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        } else if(type == Report.TimeType.MONTH) {
            var data = this.lampStatisticsMapper.selectThisMonthGroupDayByOrganId(organId);
            if(data != null && !data.isEmpty()) {
                var tmp = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    var dateTime = MyDateTime.toLocalDate(d.id);
                    var weekFields = WeekFields.of(Locale.getDefault());
                    detail.key = String.valueOf(dateTime.get(weekFields.weekOfMonth()));
                    detail.electricity = d.electricity;
                    detail.fullElectricity = d.electricity;
                    return detail;
                }).collect(Collectors.toList());
                var map = new HashMap<String, Report.Detail>();
                var keys = tmp.stream().map(d -> d.key).distinct();
                keys.forEach(k -> {
                    tmp.forEach(d -> {
                        var detail = map.get(k);
                        if(d.key.equals(k)) {
                            if(detail != null) {
                                detail.electricity = detail.electricity + d.electricity;
                                detail.fullElectricity = detail.fullElectricity + d.fullElectricity;
                            } else {
                                map.put(k, d);
                            }
                        }
                    });
                });
                obj.details = new ArrayList<>(map.values());
                obj.electricitySummary = data.stream().mapToDouble(d->d.electricity).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.electricity).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        } else if(type == Report.TimeType.YEAR) {
            var data = this.lampStatisticsMapper.selectThisYearGroupMonthByOrganId(organId);
            if(data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.electricity;
                    detail.fullElectricity = d.electricity;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d->d.electricity).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.electricity).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        }
        return null;
    }

    @Override
    public Report getReport(String organId, Report.TimeType type, LocalDateTime begin, LocalDateTime end) {
        var obj = new Report();
        obj.organId = organId;
        if(type == Report.TimeType.WEEK) {
            // 查看本周的统计
            var data = this.lampStatisticsMapper.selectThisWeekGroupDayByOrganId(organId);
            if(data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.electricity;
                    detail.fullElectricity = d.electricity;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d->d.electricity).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.electricity).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        } else if(type == Report.TimeType.MONTH) {
            var data = this.lampStatisticsMapper.selectThisMonthGroupDayByOrganId(organId);
            if(data != null && !data.isEmpty()) {
                var tmp = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    var dateTime = MyDateTime.toLocalDate(d.id);
                    var weekFields = WeekFields.of(Locale.getDefault());
                    detail.key = String.valueOf(dateTime.get(weekFields.weekOfMonth()));
                    detail.electricity = d.electricity;
                    detail.fullElectricity = d.electricity;
                    return detail;
                }).collect(Collectors.toList());
                var map = new HashMap<String, Report.Detail>();
                var keys = tmp.stream().map(d -> d.key).distinct();
                keys.forEach(k -> {
                    tmp.forEach(d -> {
                        var detail = map.get(k);
                        if(d.key.equals(k)) {
                            if(detail != null) {
                                detail.electricity = detail.electricity + d.electricity;
                                detail.fullElectricity = detail.fullElectricity + d.fullElectricity;
                            } else {
                                map.put(k, d);
                            }
                        }
                    });
                });
                obj.details = new ArrayList<>(map.values());
                obj.electricitySummary = data.stream().mapToDouble(d->d.electricity).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.electricity).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        } else if(type == Report.TimeType.YEAR) {
            var data = this.lampStatisticsMapper.selectThisYearGroupMonthByOrganId(organId);
            if(data != null && !data.isEmpty()) {
                obj.details = data.stream().map(d -> {
                    var detail = new Report.Detail();
                    detail.key = d.id;
                    detail.electricity = d.electricity;
                    detail.fullElectricity = d.electricity;
                    return detail;
                }).collect(Collectors.toList());
                obj.electricitySummary = data.stream().mapToDouble(d->d.electricity).sum();
                obj.fullElectricitySummary = data.stream().mapToDouble(d->d.electricity).sum();
                obj.onlineSummary = data.stream().mapToDouble(d->d.avgOnline).average().orElse(0.0);
                obj.faultSummary = data.stream().mapToDouble(d->d.avgFault).average().orElse(0.0);
                obj.lightSummary = data.stream().mapToDouble(d->d.avgLight).average().orElse(0.0);
            }
            return obj;
        }
        return null;
    }

    @Override
    public List<LampAlarmOutput> getAlarmList(String organId) {
        if(!MyString.isUuid(organId)) return null;
        var models = this.lampAlarmMapper.selectByOrganId(organId);
        if(models == null || models.isEmpty()) return null;
        return models.stream().map(m -> {
            var tmp = new LampAlarmOutput();
            tmp.id = m.id;
            tmp.at = MyDateTime.toTimestamp(m.at);
            tmp.content = m.content;
            tmp.lampId = m.lampId;
            tmp.lampName = m.lampName;
            tmp.regionName = m.regionName;
            tmp.status = m.status.getDescribe();
            tmp.type = m.type.getDescribe();
            return tmp;
        }).collect(Collectors.toList());
    }

    @Override
    public void statistics(RawData rawData, String name) {
        try {
            rawData.value = rawData.value.toString().replaceAll("'","\"");
            // 首先把数据存入redis
            var map = this.redissonClient.getMap(Config.getRedisRealtimeKey(rawData.imei));
            var lastData = (RealTimeData)map.get(name); // 上一次的数据
            var realTimeData = new RealTimeData();
            realTimeData.describe = name;
            realTimeData.value = rawData.value;
            realTimeData.at = MyDateTime.toTimestamp(rawData.at);
            map.put(name, realTimeData); // 更新实时缓存
            var obj = this.objectMapper.readValue(rawData.value.toString(), LampStatistics.class);
            var electricity = MyNumber.getDouble(map.get("electricity")); // 上一次累计的电量
            var ep = obj.EP.get(1);
            if(electricity == null) {
                if(ep == null || ep <= 0) {
                    electricity = 0.0;
                } else {
                    electricity = ep; // 把这一次的数据累计上去
                }
            }else {
                if(ep != null && ep > 0) {
                    electricity = electricity + ep; // 把这一次的数据累计上去
                }
            }

            map.put("light", obj.B > 0); // 当前灯的亮度状态
            map.put("fault", false); // 当前灯的故障状态
            map.put("electricity", electricity);
            var status = (Integer) map.get("status");
            if(status == null) { // 如果设备的在线状态未知，则手动刷新下
                status = this.oneNetService.isDeviceOnline(rawData.imei) ? 1 : 0;
                map.put("status", status); // 如果设备的在线状态为空，则更新设备的在线状态
            }
            var ids = this.lampMapper.selectLampRegionOrganIdByImei(rawData.imei);
            var lastUpdateStatisticsTime = (Long) map.get("lastUpdateStatisticsTime"); // 上次更新数据库的时间
            if(lastUpdateStatisticsTime == null) {
                // 更新数据库
                var model = new LampStatisticsModel();
                model.lampId = ids.lampId;
                model.regionId = ids.regionId;
                model.organId = ids.organId;
                model.online = this.oneNetService.isDeviceOnline(rawData.imei);
                model.light =  obj.B > 0;
                model.fault = false; // 故障暂时不做判断
                model.electricity = electricity; // 到目前为止累计电能
                model.updateTime = rawData.at;
                this.lampStatisticsMapper.insert(model);
                map.put("lastUpdateStatisticsTime", MyDateTime.toTimestamp(LocalDateTime.now()));
                map.put("electricity", 0); // 清空累计，也就是我只保存这一个小时的电量，下个周期从0开始重新计算
            } else {
                // 存在上一次的更新时间，则看上一次的更新时间，到现在有没有达到一个小时（至少）
                var lastTime = MyDateTime.toLocalDateTime(lastUpdateStatisticsTime);
                if(lastTime != null) {
                    var now = LocalDateTime.now();
                    var hours = Duration.between(lastTime, now).abs().toHours();
                    if(hours >= 1) {
                        // 更新数据库
                        var model = new LampStatisticsModel();
                        model.lampId = ids.lampId;
                        model.regionId = ids.regionId;
                        model.organId = ids.organId;
                        model.online = this.oneNetService.isDeviceOnline(rawData.imei);
                        model.light =  obj.B > 0;
                        model.fault = false; // 故障暂时不做判断
                        model.electricity = electricity; // 到目前为止累计电能
                        model.updateTime = rawData.at;
                        this.lampStatisticsMapper.insert(model);
                        map.put("lastUpdateStatisticsTime", MyDateTime.toTimestamp(LocalDateTime.now()));
                        map.put("electricity", 0); // 清空累计，也就是我只保存这一个小时的点亮，下个周期从0开始重新计算
                    }
                }
            }
            // 最后将收到的数据推送到页面
            var statistics = new LampStatistic();
            var msg = new LampStatistic.Message();
            msg.id = ids.lampId;
            msg.voltage =  new LampStatistic.Message.ValueError<>(obj.V, obj.V != null && obj.V <= 0.1);
            msg.brightness = new LampStatistic.Message.ValueError<>(obj.B, obj.B != null && obj.B >= 0 && obj.B <= 100);
            msg.electricity = new LampStatistic.Message.ValueError<>(obj.I.get(1), obj.I.get(1) != null && obj.I.get(1) <= 0.1);
            msg.energy = new LampStatistic.Message.ValueError<>(obj.EP.get(1), obj.EP.get(1) != null && obj.EP.get(1) <= 1.5);
            msg.power = new LampStatistic.Message.ValueError<>(obj.PP.get(1), obj.PP.get(1) != null && obj.EP.get(1) <= 1.5);
            var pp = obj.PP.get(1);
            var ps = obj.PS.get(1);
            if(ps == null || ps <= 0) {
                msg.powerFactor = new LampStatistic.Message.ValueError<>(0.0, true);
            } else {
                msg.powerFactor = new LampStatistic.Message.ValueError<>(pp / ps, false);
            }
            msg.rate = new LampStatistic.Message.ValueError<>(obj.HZ, obj.HZ != null && obj.HZ <= 60);
            msg.updateTime = MyDateTime.toTimestamp(rawData.at);
            statistics.message = msg;
            var json = this.objectMapper.writeValueAsString(statistics);
            LampStatisticsHandler.sendAll(json);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }
    }
}
