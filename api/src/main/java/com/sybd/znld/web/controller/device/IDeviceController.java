package com.sybd.znld.web.controller.device;

import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.dto.DeviceIdsAndDataStreams;
import com.sybd.znld.model.lamp.dto.RegionsAndDataStreams;
import com.sybd.znld.model.ministar.dto.DeviceSubtitle;
import com.sybd.znld.model.ministar.dto.Subtitle;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.onenet.dto.BaseResult;
import com.sybd.znld.model.onenet.dto.OneNetExecuteArgs;
import com.sybd.znld.web.controller.device.dto.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "data/last/deviceId/{deviceId:^[1-9]\\d*$}/dataStreams", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    LastDataResults getLastDataWithDeviceIdAndDataStreams(@PathVariable(name = "deviceId") Integer deviceId, @RequestBody List<String> dataStreams, HttpServletRequest request);

    @GetMapping(value = "data/last/deviceId/{deviceId:^[1-9]\\d*$}/dataStream/{dataStream}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    BaseApiResult getLastDataWithDeviceIdAndDataStream(@PathVariable(name = "deviceId") Integer deviceId, @PathVariable(name = "dataStream") String dataStream, HttpServletRequest request);

    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, Integer limit, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, Integer limit, String cursor, HttpServletRequest request);
    HistoryDataResult getHistoryData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp, Integer limit, String cursor, String sort, HttpServletRequest request);

    ExecuteResult execute(Integer deviceId, OneNetExecuteArgs command, HttpServletRequest request);
    DeviceIdAndNameResult getDeviceIdAndName(String userId, HttpServletRequest request);
    MiniStarResult newMiniStar(List<OneNetExecuteArgs> data, HttpServletRequest request);

    @GetMapping(value = "ministar/history/{count}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    MiniStarHistoryResult getMiniStarHistory(@RequestHeader("userId") String userId, @PathVariable(name = "count") Integer count);

    @PostMapping(value = "ministar/deviceId/{deviceId:^[1-9]\\d*$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    MiniStarResult newDeviceMiniStar(@PathVariable(name = "deviceId") Integer deviceId, @RequestBody Subtitle data, HttpServletRequest request);

    @PutMapping(value = "status/deviceId/{deviceId:^[1-9]\\d*$}/dataStream/{dataStream}/value/{value}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    PushResult pushByDeviceIdOfDataStream(@PathVariable("deviceId") Integer deviceId, @PathVariable(name = "dataStream") String dataStream, @PathVariable(name = "value") Object value);

    @GetMapping(value = "status/deviceId/{deviceId:^[1-9]\\d*$}/dataStream/{dataStream}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    PullResult pullByDeviceIdOfDataStream(@PathVariable("deviceId") Integer deviceId, @PathVariable(name = "dataStream") String dataStream);

    @PutMapping(value = "status/deviceId/{deviceId:^[1-9]\\d*$}/dataStreams/value/{value}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    PushResult pushByDeviceIdOfDataStreams(@PathVariable("deviceId") Integer deviceId, @RequestBody List<String> dataStreams, @PathVariable(name = "value") Object value);

    @PostMapping(value = "status/deviceId/{deviceId:^[1-9]\\d*$}/dataStreams", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    PullResult pullByDeviceIdOfDataStreams(@PathVariable("deviceId") Integer deviceId, @RequestBody List<String> dataStreams);

    @PutMapping(value = "status/region/{region}/dataStream/{dataStream}/value/{value}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    PushRegionResult pushByRegionOfDataStream(@PathVariable(name = "region") String region, @PathVariable(name = "dataStream") String dataStream, @PathVariable(name = "value") Object value);

    @GetMapping(value = "status/region/{region}/dataStream/{dataStream}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    PullRegionResult pullByRegionOfDataStream(@PathVariable(name = "region") String region, @PathVariable(name = "dataStream") String dataStream);

    @GetMapping(value = "status/region/{region}/dataStream/{dataStream}/angle", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    PullRegionResult pullByRegionOfDataStreamWithAngle(@PathVariable(name = "region") String region, @PathVariable(name = "dataStream") String dataStream);

    @PutMapping(value = "status/region/{region}/dataStreams/value/{value}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    PushRegionResult pushByRegionOfDataStreams(@PathVariable(name = "region") String region, @RequestBody List<String> dataStreams, @PathVariable(name = "value") Object value);

    @PostMapping(value = "status/region/{region}/dataStreams", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    PullRegionResult pullByRegionOfDataStreams(@PathVariable(name = "region") String region, @RequestBody List<String> dataStreams);

    @GetMapping(value = "status/deviceId/{deviceId:^[1-9]\\d*$}/dataStream/angle", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    AngleResult getAngleStatus(@PathVariable("deviceId") Integer deviceId);

    @GetMapping(value = "status/region/{region}/dataStream/angle", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    AngleRegionResult getRegionAngleStatus(@PathVariable("region") String region);
}
