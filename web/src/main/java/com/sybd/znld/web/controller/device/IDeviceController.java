package com.sybd.znld.web.controller.device;

import com.sybd.znld.web.controller.device.dto.*;
import com.sybd.znld.web.onenet.dto.OneNetExecuteArgs;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IDeviceController {
    DataResult getAvgHistoryData(String regionId, String dataStreamId, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResult getAvgHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    LastDataResult getLastData(Integer deviceId, String dataStreamId, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, Integer limit, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, Integer limit, String cursor, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, Integer limit, String cursor, String sort, HttpServletRequest request);
    ExecuteResult execute(Integer deviceId, OneNetExecuteArgs command, HttpServletRequest request);
    DeviceIdAndNameResult getDeviceIdAndName(String userId, HttpServletRequest request);
    ExecuteResult newMiniStar(List<OneNetExecuteArgs> data, HttpServletRequest request);
}
