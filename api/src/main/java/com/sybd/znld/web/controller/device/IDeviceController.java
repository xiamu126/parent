package com.sybd.znld.web.controller.device;

import com.sybd.znld.model.lamp.dto.DeviceIdsAndDataStreams;
import com.sybd.znld.model.lamp.dto.RegionsAndDataStreams;
import com.sybd.znld.web.controller.device.dto.*;
import com.sybd.znld.web.onenet.dto.OneNetExecuteArgs;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IDeviceController {
    DataResultsMap getAvgHistoryDataWithRegionsAndDataStreams(RegionsAndDataStreams regionsAndDataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResults getAvgHistoryDataWithRegionAndDataStreams(String region, List<String> dataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResults getAvgHistoryDataWithRegionsAndDataStream(List<String> regions, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    DataResultsMap getAvgHistoryDataWithDeviceIdsAndDataStreams(DeviceIdsAndDataStreams deviceIdsAndDataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResults getAvgHistoryDataWithDeviceIdAndDataStreams(Integer deviceId, List<String> dataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResults getAvgHistoryDataWithDeviceIdsAndDataStream(List<Integer> deviceIds, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    DataResult getAvgHistoryDataWithRegionAndDataStream(String region, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResult getAvgHistoryDataWithDeviceIdAndDataStream(Integer deviceId, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    DataResultsMap getMaxHistoryDataWithRegionsAndDataStreams(RegionsAndDataStreams regionsAndDataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResultsMap getMaxHistoryDataWithDeviceIdsAndDataStreams(DeviceIdsAndDataStreams deviceIdsAndDataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    DataResults getMaxHistoryDataWithRegionsAndDataStream(List<String> regions, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResults getMaxHistoryDataWithDeviceIdsAndDataStream(List<Integer> deviceIds, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    DataResult getMaxHistoryDataWithRegionAndDataStream(String region, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResult getMaxHistoryDataWithDeviceIdAndDataStream(Integer deviceId, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    DataResultsMap getMinHistoryDataWithRegionsAndDataStreams(RegionsAndDataStreams regionsAndDataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResultsMap getMinHistoryDataWithDeviceIdsAndDataStreams(DeviceIdsAndDataStreams deviceIdsAndDataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    DataResults getMinHistoryDataWithRegionsAndDataStream(List<String> regions, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResults getMinHistoryDataWithDeviceIdsAndDataStream(List<Integer> deviceIds, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    DataResult getMinHistoryDataWithRegionAndDataStream(String region, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    DataResult getMinHistoryDataWithDeviceIdAndDataStream(Integer deviceId, String dataStream, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);

    LastDataResultsMap getLastDataWithDeviceIdsAndDataStreams(DeviceIdsAndDataStreams deviceIdsAndDataStreams, HttpServletRequest request);
    LastDataResults getLastDataWithDeviceIdAndDataStreams(List<Integer> deviceIds, String dataStream, HttpServletRequest request);
    LastDataResults getLastDataWithDeviceIdAndDataStreams(Integer deviceId, List<String> dataStreams, HttpServletRequest request);
    LastDataResult getLastDataWithDeviceIdAndDataStream(Integer deviceId, String dataStream, HttpServletRequest request);

    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, Integer limit, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, Integer limit, String cursor, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, Integer limit, String cursor, String sort, HttpServletRequest request);

    ExecuteResult execute(Integer deviceId, OneNetExecuteArgs command, HttpServletRequest request);
    DeviceIdAndNameResult getDeviceIdAndName(String userId, HttpServletRequest request);
    ExecuteResult newMiniStar(List<OneNetExecuteArgs> data, HttpServletRequest request);
}
