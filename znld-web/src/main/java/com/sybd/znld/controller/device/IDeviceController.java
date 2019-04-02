package com.sybd.znld.controller.device;

import com.sybd.znld.controller.device.dto.*;
import com.sybd.znld.core.ApiResult;
import com.sybd.znld.onenet.dto.OneNetExecuteArgs;
import com.sybd.znld.onenet.dto.OneNetExecuteArgsEx;

import javax.servlet.http.HttpServletRequest;

public interface IDeviceController {
    LastDataResult getLastData(Integer deviceId, String dataStreamId, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId,
                                 String dataStreamId,
                                 Long beginTimestamp,
                                 Long endTimestamp, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId,
                             String dataStreamId,
                             Long beginTimestamp,
                             Long endTimestamp,
                             Integer limit, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId,
                             String dataStreamId,
                             Long beginTimestamp,
                             Long endTimestamp,
                             Integer limit,
                             String cursor, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId,
                             String dataStreamId,
                             Long beginTimestamp,
                             Long endTimestamp,
                             Integer limit,
                             String cursor,
                             String sort, HttpServletRequest request);
    ExecuteResult execute(Integer deviceId, OneNetExecuteArgs command, HttpServletRequest request);
    DeviceIdAndNameResult getDeviceIdAndName(HttpServletRequest request);
    CheckedResourcesResult getCheckedResources(Integer deviceId, HttpServletRequest request);
}
