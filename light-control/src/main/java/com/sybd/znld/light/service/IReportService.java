package com.sybd.znld.light.service;

import com.sybd.znld.light.service.dto.Report;

import java.time.LocalDateTime;

public interface IReportService {
    Report getReport(String organId, Report.TimeType type);
    Report getReport(String organId, Report.TimeType type, LocalDateTime begin, LocalDateTime end);
}
