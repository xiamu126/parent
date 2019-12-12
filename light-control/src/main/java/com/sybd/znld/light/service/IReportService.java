package com.sybd.znld.light.service;

import com.sybd.znld.light.service.dto.Report;

public interface IReportService {
    Report getReport(String organId, String type);
}
