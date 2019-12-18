package com.sybd.znld.light.service;

import com.sybd.znld.light.service.dto.Report;
import com.sybd.znld.mapper.lamp.LampStatisticsMapper;
import com.sybd.znld.util.MyDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportService implements IReportService {
    private final LampStatisticsMapper lampStatisticsMapper;

    @Autowired
    public ReportService(LampStatisticsMapper lampStatisticsMapper) {
        this.lampStatisticsMapper = lampStatisticsMapper;
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
}
