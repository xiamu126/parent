package com.sybd.znld.web.controller.device;

import com.sybd.znld.model.lamp.dto.DeviceIdsAndDataStreams;
import com.sybd.znld.model.lamp.dto.RegionsAndDataStreams;
import com.sybd.znld.web.controller.device.dto.*;
import com.sybd.znld.web.onenet.dto.OneNetExecuteArgs;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IDeviceController {
    DataResultsMap getAvgHistoryData(RegionsAndDataStreams regionsAndDataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResults getAvgHistoryData(String region, List<String> dataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResults getAvgHistoryData(List<String> regions, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    DataResultsMap getAvgHistoryDataWithDeviceIdsAndDataStreams(DeviceIdsAndDataStreams deviceIdsAndDataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResults getAvgHistoryDataWithDeviceIdAndDataStreams(Integer deviceId, List<String> dataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResults getAvgHistoryDataWithDeviceIdsAndDataStream(List<Integer> deviceIds, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    DataResult getAvgHistoryData(String regionId, String dataStreamId, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResult getAvgHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    DataResult getMaxHistoryData(String regionId, String dataStreamId, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResult getMaxHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    DataResult getMinHistoryData(String regionId, String dataStreamId, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResult getMinHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    LastDataResult getLastData(Integer deviceId, String dataStreamId, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, Integer limit, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, Integer limit, String cursor, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, Integer limit, String cursor, String sort, HttpServletRequest request);
    ExecuteResult execute(Integer deviceId, OneNetExecuteArgs command, HttpServletRequest request);
    DeviceIdAndNameResult getDeviceIdAndName(String userId, HttpServletRequest request);
    ExecuteResult newMiniStar(List<OneNetExecuteArgs> data, HttpServletRequest request);
}
