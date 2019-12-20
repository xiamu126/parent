package com.sybd.znld.light.service;

import com.sybd.znld.light.service.dto.Report;
import com.sybd.znld.model.environment.RawData;
import com.sybd.znld.model.lamp.LampAlarmModel;
import com.sybd.znld.model.lamp.dto.LampAlarmOutput;

import java.time.LocalDateTime;
import java.util.List;

public interface IReportService {
    Report getReport(String organId, Report.TimeType type);
    Report getReport(String organId, Report.TimeType type, LocalDateTime begin, LocalDateTime end);
    List<LampAlarmOutput> getAlarmList(String organId);
    void statistics(RawData rawData, String name);
}
