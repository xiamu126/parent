package com.sybd.znld.light.service;

import com.sybd.znld.model.lamp.dto.LampAlarm;
import com.sybd.znld.model.lamp.dto.Report;
import com.sybd.znld.model.environment.RawData;

import java.time.LocalDateTime;
import java.util.List;

public interface IReportService {
    Report getReport(String organId, Report.TimeType type);
    Report getReport(String organId, Report.TimeType type, LocalDateTime begin, LocalDateTime end);
    List<LampAlarm.Message> getAlarmList(String organId);
    void statistics(RawData rawData, String name);
}
