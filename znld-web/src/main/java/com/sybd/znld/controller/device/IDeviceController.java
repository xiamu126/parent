package com.sybd.znld.controller.device;

import com.sybd.znld.controller.device.dto.*;
import com.sybd.znld.core.ApiResult;
import com.sybd.znld.onenet.dto.OneNetExecuteArgsEx;

public interface IDeviceController {
    LastDataResult getLastData(Integer deviceId, String dataStreamId);
    HistoryDataResult getHistoryData(Integer deviceId,
                                 String dataStreamId,
                                 Long beginTimestamp,
                                 Long endTimestamp);
    HistoryDataResult getHistoryData(Integer deviceId,
                             String dataStreamId,
                             Long beginTimestamp,
                             Long endTimestamp,
                             Integer limit);
    HistoryDataResult getHistoryData(Integer deviceId,
                             String dataStreamId,
                             Long beginTimestamp,
                             Long endTimestamp,
                             Integer limit,
                             String cursor);
    HistoryDataResult getHistoryData(Integer deviceId,
                             String dataStreamId,
                             Long beginTimestamp,
                             Long endTimestamp,
                             Integer limit,
                             String cursor,
                             String sort);
    ExecuteResult execute(Integer deviceId, OneNetExecuteArgsEx command);
    DeviceIdAndNameResult getDeviceIdAndName();
    CheckedResourcesResult getCheckedResources(Integer deviceId);
}
