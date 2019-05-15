package com.sybd.znld.web.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.model.lamp.dto.DeviceIdsAndDataStreams;
import com.sybd.znld.model.lamp.dto.RegionsAndDataStreams;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.lamp.IRegionService;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import com.sybd.znld.web.controller.device.dto.*;
import com.sybd.znld.web.onenet.IOneNetService;
import com.sybd.znld.web.onenet.OneNetService;
import com.sybd.znld.web.onenet.dto.CommandParams;
import com.sybd.znld.web.onenet.dto.OneNetExecuteArgs;
import com.sybd.znld.model.onenet.OneNetKey;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Api(tags = "设备接口")
@RestController
@RequestMapping("/api/v1/device")
public class DeviceController implements IDeviceController {
    private final RedissonClient redissonClient;
    private final IOneNetService oneNet;
    private final ILampService lampService;
    private final ProjectConfig projectConfig;
    private final IUserService userService;
    private final IRegionService regionService;
    @Autowired
    public DeviceController(RedissonClient redissonClient,
                            IOneNetService oneNet,
                            ILampService lampService,
                            ProjectConfig projectConfig,
                            IUserService userService,
                            IRegionService regionService) {
        this.redissonClient = redissonClient;
        this.oneNet = oneNet;
        this.lampService = lampService;
        this.projectConfig = projectConfig;
        this.userService = userService;
        this.regionService = regionService;
    }

    private Map<Integer, String> getResourceByHour(Integer deviceId, OneNetKey oneNetKey, LocalDateTime begin) {
        var end = MyDateTime.maxOfDay(begin);
        log.debug(begin.toString());
        log.debug(end.toString());
        return getResourceByHour(deviceId, oneNetKey, begin, end);
    }

    private Map<Integer, String> getResourceByHour(Integer deviceId, OneNetKey oneNetKey, LocalDateTime begin, LocalDateTime end){
        try{
            if(begin.isEqual(end)){
                log.debug("开始时间等于结束时间");
                return null;
            }
            var result = this.oneNet.getHistoryDataStream(deviceId, oneNetKey.toDataStreamId(), begin, end, 5000, null, null);//过去24小时内的数据
            var data = (result.getData().getDataStreams().get(0)).getDataPoints();
            var theSet = new HashSet<Integer>();
            var sortedMap = new TreeMap<Integer, String>();
            for(var theData : data){
                var str = theData.getAt();
                var theDate = LocalDateTime.parse(str, DateTimeFormatter.ofPattern(MyDateTime.format2));
                var hour = theDate.getHour();
                if (!theSet.contains(hour)) {
                    theSet.add(hour);
                    sortedMap.put(hour, theData.value);
                }
            }
            return sortedMap;
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
       return null;
    }

    @ApiOperation(value = "获取设备的某个资源历史数据，指定开始时间、结束时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/history/pretty/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public PrettyHistoryDataResult getPrettyHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                                        @PathVariable(name = "dataStreamId") String dataStreamId,
                                                        @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                        @PathVariable(name = "endTimestamp") Long endTimestamp){
        var result = getResourceByHour(deviceId, OneNetKey.from(dataStreamId), MyDateTime.toLocalDateTime(beginTimestamp), MyDateTime.toLocalDateTime(endTimestamp));
        if(result == null) return PrettyHistoryDataResult.fail("获取数据为空");
        return PrettyHistoryDataResult.success(result);
    }

    @ApiOperation(value = "获取设备的某个资源历史数据，仅指定开始时间，结束时间为默认当前时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/history/pretty/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public PrettyHistoryDataResult getPrettyHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                                        @PathVariable(name = "dataStreamId") String dataStreamId,
                                                        @PathVariable(name = "beginTimestamp") Long beginTimestamp){
        var result = getResourceByHour(deviceId, OneNetKey.from(dataStreamId), MyDateTime.toLocalDateTime(beginTimestamp), LocalDateTime.now());
        if(result == null) return PrettyHistoryDataResult.fail("获取数据为空");
        return PrettyHistoryDataResult.success(result);
    }

    @ApiOperation(value = "获取多个区域的某时间段内的多个资源的各自的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionsAndDataStreams", value = "区域id集合或名字集合与数据流Id集合或具体的资源名称集合", required = true, dataType = "Object", paramType = "body"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @PostMapping(value = "data/avg/regions/dataStreams/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResultsMap getAvgHistoryDataWithRegionsAndDataStreams(@RequestBody RegionsAndDataStreams regionsAndDataStreams,
                                                                     @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                     @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(regionsAndDataStreams == null || !regionsAndDataStreams.isValid() || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResultsMap.fail("错误的参数");
            }
            var result = new HashMap<String, Map<String, String>>();
            regionsAndDataStreams.regions.forEach(region -> {
                var map = new HashMap<String, String>();
                regionsAndDataStreams.dataStreams.forEach(dataStream -> {
                    var tmp = this.getAvgHistoryDataWithRegionAndDataStream(region, dataStream, beginTimestamp, endTimestamp, request);
                    if(tmp != null && tmp.isOk()) map.put(dataStream, tmp.value);
                });
                if(!map.isEmpty()){
                    result.put(region, map);
                }
            });
            if(!result.isEmpty()) return DataResultsMap.success(result);
            return DataResultsMap.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResultsMap.fail("获取数据失败");
    }

    @ApiOperation(value = "获取某个区域的某时间段内的多个资源的各自的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "region", value = "区域id或名字", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "dataStreams", value = "查看的数据流Id集合或具体的资源名称集合", required = true, dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @PostMapping(value = "data/avg/region/{region}/dataStreams/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResults getAvgHistoryDataWithRegionAndDataStreams(@PathVariable(name = "region") String region,
                                                                 @RequestBody List<String> dataStreams,
                                                                 @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                 @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(MyString.isEmptyOrNull(region) || dataStreams == null || dataStreams.isEmpty() || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResults.fail("错误的参数");
            }
            var map = new HashMap<String, String>();
            dataStreams.forEach(dataStream -> {
                if(!MyString.isEmptyOrNull(dataStream)){
                    var tmp = this.getAvgHistoryDataWithRegionAndDataStream(region, dataStream, beginTimestamp, endTimestamp, request);
                    if(tmp != null && tmp.isOk()) map.put(dataStream, tmp.value);
                }
            });
            if(!map.isEmpty()) return DataResults.success(map);
            return DataResults.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResults.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个区域的某时间段内的某一资源的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regions", value = "区域的id集合或具体的名称集合", required = true, dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "dataStream", value = "查看的数据流Id或资源名称", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @PostMapping(value = "data/avg/regions/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResults getAvgHistoryDataWithRegionsAndDataStream(@RequestBody List<String> regions,
                                                                 @PathVariable(name = "dataStream") String dataStream,
                                                                 @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                 @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(regions == null || regions.isEmpty() || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResults.fail("错误的参数");
            }
            var map = new HashMap<String, String>();
            regions.forEach(region -> {
                if(!MyString.isEmptyOrNull(region)){
                    var tmp = this.getAvgHistoryDataWithRegionAndDataStream(region, dataStream, beginTimestamp, endTimestamp, request);
                    if(tmp != null && tmp.isOk()) map.put(region, tmp.value);
                }
            });
            if(!map.isEmpty()) return DataResults.success(map);
            return DataResults.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResults.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个设备的某时间段内的多个资源的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIdsAndDataStreams", value = "设备id集合与数据流Id集合或资源名称集合", required = true, dataType = "Object", paramType = "body"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @PostMapping(value = "data/avg/deviceIds/dataStreams/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResultsMap getAvgHistoryDataWithDeviceIdsAndDataStreams(@RequestBody DeviceIdsAndDataStreams deviceIdsAndDataStreams,
                                                                       @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                       @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(deviceIdsAndDataStreams == null || !deviceIdsAndDataStreams.isValid() || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResultsMap.fail("参数错误");
            }
            var result = new HashMap<String, Map<String, String>>();
            deviceIdsAndDataStreams.deviceIds.forEach(deviceId -> {
                var map = new HashMap<String, String>();
                deviceIdsAndDataStreams.dataStreams.forEach(dataStream -> {
                    if(!MyString.isEmptyOrNull(dataStream) && MyNumber.isPositive(deviceId)){
                        var tmp = this.getAvgHistoryDataWithDeviceIdAndDataStream(deviceId, dataStream, beginTimestamp, endTimestamp, request);
                        if(tmp != null && tmp.isOk()) {
                            map.put(dataStream, tmp.value);
                        }
                    }
                });
                if(!map.isEmpty()){
                    result.put(deviceId.toString(), map);
                }
            });
            if(!result.isEmpty()) return DataResultsMap.success(result);
            return DataResultsMap.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResultsMap.fail("获取数据失败");
    }

    @ApiOperation(value = "获取某个设备的某时间段内的多个资源的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "dataStreams", value = "数据流Id集合或资源名称集合", required = true, dataType = "List", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @PostMapping(value = "data/avg/deviceId/{deviceId}/dataStreams/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResults getAvgHistoryDataWithDeviceIdAndDataStreams(@PathVariable(name = "deviceId") Integer deviceId,
                                                                   @RequestBody List<String> dataStreams,
                                                                   @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                   @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(!MyNumber.isPositive(deviceId) || dataStreams == null || dataStreams.isEmpty() || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResults.fail("参数错误");
            }
            var map = new HashMap<String, String>();
            dataStreams.forEach(dataStream -> {
                if(!MyString.isEmptyOrNull(dataStream)){
                    var tmp = this.getAvgHistoryDataWithDeviceIdAndDataStream(deviceId, dataStream, beginTimestamp, endTimestamp, request);
                    if(tmp != null && tmp.isOk()){
                        map.put(dataStream, tmp.value);
                    }
                }
            });
            if(!map.isEmpty())return DataResults.success(map);
            return DataResults.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResults.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个设备的某时间段内的某一资源的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIds", value = "设备id集合", required = true, dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "dataStream", value = "查看的数据流Id或资源名称", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @PostMapping(value = "data/avg/deviceIds/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResults getAvgHistoryDataWithDeviceIdsAndDataStream(@RequestBody List<Integer> deviceIds,
                                                                   @PathVariable(name = "dataStream") String dataStream,
                                                                   @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                   @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(deviceIds == null || deviceIds.isEmpty() || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResults.fail("参数错误");
            }
            var map = new HashMap<String, String>();
            deviceIds.forEach(deviceId -> {
                if(MyNumber.isPositive(deviceId)){
                    var tmp = this.getAvgHistoryDataWithDeviceIdAndDataStream(deviceId, dataStream, beginTimestamp, endTimestamp, request);
                    if(tmp != null && tmp.isOk()){
                        map.put(deviceId.toString(), tmp.value);
                    }
                }
            });
            if(!map.isEmpty()) return DataResults.success(map);
            return DataResults.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResults.fail("获取数据失败");
    }

    @ApiOperation(value = "获取某个区域的某个时间段内的某个资源的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "region", value = "区域Id或名字", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "dataStream", value = "资源Id或名字", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/avg/region/{region}/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResult getAvgHistoryDataWithRegionAndDataStream(@PathVariable(name = "region") String region,
                                                               @PathVariable(name = "dataStream") String dataStream,
                                                               @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                               @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(MyString.isEmptyOrNull(region) || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResult.fail("错误的参数");
            }
            String regionId = null;
            if(MyString.isUuid(region)){ // 为区域id
                regionId = region;
            }else { // 可能是区域名字
                var regionModel = this.regionService.getRegionByName(region);
                if(regionModel != null) regionId = regionModel.id;
            }
            if(regionId == null) return DataResult.fail("获取数据失败");

            var lamps = this.lampService.getLampsByRegionId(regionId);
            if(lamps == null || lamps.isEmpty()){
                return DataResult.fail("获取数据失败");
            }
            var sum = 0.0;
            var count = 0;
            for(var lamp: lamps){
                var ret = this.getAvgHistoryDataWithDeviceIdAndDataStream(lamp.deviceId, dataStream, beginTimestamp, endTimestamp, request);
                if(ret != null && ret.isOk()){
                    var d = MyNumber.getDouble(ret.value);
                    if(d != null) {
                        sum += d;
                        count++;
                    }
                }
            }
            if(count > 0){
                var avg = sum / count;
                return DataResult.success(MyNumber.toString(avg));
            }
            return DataResult.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取某个设备的某个时间段内的某个资源的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStream", value = "资源id或名称", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/avg/deviceId/{deviceId:^[1-9]\\d*$}/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResult getAvgHistoryDataWithDeviceIdAndDataStream(@PathVariable(name = "deviceId") Integer deviceId,
                                                                 @PathVariable(name = "dataStream") String dataStream,
                                                                 @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                 @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(!MyNumber.isPositive(deviceId) || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)) {
                return DataResult.fail("错误的参数");
            }
            String dataStreamId = null;
            if(OneNetKey.isDataStreamId(dataStream)){ // 为资源id
                dataStreamId = dataStream;
            }else { // 可能为资源名称
                var tmp = this.lampService.getDataStreamIdByResourceName(dataStream);
                if(!MyString.isEmptyOrNull(tmp)){
                    dataStreamId = tmp;
                }
            }
            if(dataStreamId == null) return DataResult.fail("获取数据失败");
            var start = MyDateTime.toLocalDateTime(beginTimestamp);
            var end = MyDateTime.toLocalDateTime(endTimestamp);
            var ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, start, end, null, null, null);
            if(ret != null && ret.isOk()){
                Supplier<Stream<Double>> streamSupplier = () -> ret.data.dataStreams.stream().map(s -> {
                    var p = s.dataPoints.get(0);
                    if(p == null) return null;
                    return MyNumber.getDouble(p.value);
                });
                var count = streamSupplier.get().filter(Objects::nonNull).count();
                var sum = streamSupplier.get().filter(Objects::nonNull).reduce(0.0, Double::sum);
                if(count > 0){
                    var avg = sum / count;
                    return DataResult.success(MyNumber.toString(avg));
                }
                return DataResult.fail("获取数据为空");
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个区域的某个时间段内的各个资源的最大值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionsAndDataStreams", value = "区域id集合或名字集合与资源id或名字集合", required = true, dataType = "Object", paramType = "body"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "Long", paramType = "path")
    })
    @PostMapping(value = "data/max/regions/dataStreams/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResultsMap getMaxHistoryDataWithRegionsAndDataStreams(@RequestBody RegionsAndDataStreams regionsAndDataStreams,
                                                                     @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                     @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(regionsAndDataStreams == null || !regionsAndDataStreams.isValid() || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResultsMap.fail("错误的参数");
            }
            var result = new HashMap<String, Map<String, String>>();
            regionsAndDataStreams.regions.forEach(region -> {
                var map = new HashMap<String, String>();
                regionsAndDataStreams.dataStreams.forEach(dataStream -> {
                    if(!MyString.isEmptyOrNull(region) && !MyString.isEmptyOrNull(dataStream)){
                        var tmp = this.getMaxHistoryDataWithRegionAndDataStream(region, dataStream, beginTimestamp, endTimestamp, request);
                        if(tmp != null && tmp.isOk()){
                            map.put(dataStream, tmp.value);
                        }
                    }
                });
                if(!map.isEmpty()){
                    result.put(region, map);
                }
            });
            if(!result.isEmpty()) return DataResultsMap.success(result);
            return DataResultsMap.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResultsMap.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个设备的某个时间段内的多个资源的最大值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIdsAndDataStreams", value = "设备id集合或名字集合与资源id或名字集合", required = true, dataType = "Object", paramType = "body"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "Long", paramType = "path")
    })
    @PostMapping(value = "data/max/deviceIds/dataStreams/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResultsMap getMaxHistoryDataWithDeviceIdsAndDataStreams(@RequestBody DeviceIdsAndDataStreams deviceIdsAndDataStreams,
                                                                       @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                       @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(deviceIdsAndDataStreams == null || !deviceIdsAndDataStreams.isValid() || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResultsMap.fail("错误的参数");
            }
            var result = new HashMap<String, Map<String, String>>();
            deviceIdsAndDataStreams.deviceIds.forEach(deviceId -> {
                var map = new HashMap<String, String>();
                deviceIdsAndDataStreams.dataStreams.forEach(dataStream -> {
                    if(MyNumber.isPositive(deviceId) && !MyString.isEmptyOrNull(dataStream)){
                        var tmp = this.getMaxHistoryDataWithDeviceIdAndDataStream(deviceId, dataStream, beginTimestamp, endTimestamp, request);
                        if(tmp != null && tmp.isOk()){
                            map.put(dataStream, tmp.value);
                        }
                    }
                });
                if(!map.isEmpty()){
                    result.put(deviceId.toString(), map);
                }
            });
            if(!result.isEmpty()) return DataResultsMap.success(result);
            return DataResultsMap.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResultsMap.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个区域的某个时间段内的某个资源的最大值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIds", value = "设备Id集合", required = true, dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "dataStream", value = "资源Id或名称", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @PostMapping(value = "data/max/regions/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResults getMaxHistoryDataWithRegionsAndDataStream(@RequestBody List<String> regions,
                                                                 @PathVariable(name = "dataStream") String dataStream,
                                                                 @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                 @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(regions == null || regions.isEmpty() || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResults.fail("错误的参数");
            }
            var map = new HashMap<String, String>();
            regions.forEach(region -> {
                if(!MyString.isEmptyOrNull(region)){
                    var tmp = this.getMaxHistoryDataWithRegionAndDataStream(region, dataStream, beginTimestamp, endTimestamp, request);
                    if(tmp != null && tmp.isOk()){
                        map.put(region, tmp.value);
                    }
                }
            });
            if(!map.isEmpty()) return DataResults.success(map);
            return DataResults.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResults.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个设备的某个时间段内的某个资源的最大值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIds", value = "设备Id集合", required = true, dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "dataStream", value = "资源Id或名称", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @PostMapping(value = "data/max/deviceIds/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResults getMaxHistoryDataWithDeviceIdsAndDataStream(@RequestBody List<Integer> deviceIds,
                                                                   @PathVariable(name = "dataStream") String dataStream,
                                                                   @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                   @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(deviceIds == null || deviceIds.isEmpty() || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResults.fail("错误的参数");
            }
            var map = new HashMap<String, String>();
            deviceIds.forEach(deviceId -> {
                if(MyNumber.isPositive(deviceId)){
                    var tmp = this.getMaxHistoryDataWithDeviceIdAndDataStream(deviceId, dataStream, beginTimestamp, endTimestamp, request);
                    if(tmp != null && tmp.isOk()){
                        map.put(deviceId.toString(), tmp.value);
                    }
                }
            });
            if(!map.isEmpty()) return DataResults.success(map);
            return DataResults.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResults.fail("获取数据失败");
    }

    @ApiOperation(value = "获取某个区域的某个时间段内的某个资源的最大值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "region", value = "区域Id或者名字", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStream", value = "资源Id或名称", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/max/region/{region}/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResult getMaxHistoryDataWithRegionAndDataStream(@PathVariable(name = "region") String region,
                                                               @PathVariable(name = "dataStream") String dataStream,
                                                               @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                               @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(MyString.isEmptyOrNull(region) || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResult.fail("错误参数");
            }
            String regionId = null;
            if(MyString.isUuid(region)){ // 为区域id
                regionId = region;
            }else { // 可能是区域名字
                var regionModel = this.regionService.getRegionByName(region);
                if(regionModel != null) regionId = regionModel.id;
            }
            if(regionId == null) return DataResult.fail("获取数据失败");

            var lamps = this.lampService.getLampsByRegionId(regionId);
            if(lamps == null || lamps.isEmpty()){
                return DataResult.fail("获取数据失败");
            }
            var result = new ArrayList<Double>();
            for(var lamp: lamps){
                var ret = this.getMaxHistoryDataWithDeviceIdAndDataStream(lamp.deviceId, dataStream, beginTimestamp, endTimestamp, request);
                if(ret != null && ret.isOk()){
                    var d = MyNumber.getDouble(ret.value);
                    if(d != null) {
                        result.add(d);
                    }
                }
            }
            var max = result.stream().max(Comparator.comparing(Double::valueOf)).orElse(null);
            if(max != null) return DataResult.success(MyNumber.toString(max));
            return DataResult.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取某个设备的某个时间段内的某个资源的最大值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "Long", paramType = "path")
    })
    @GetMapping(value = "data/max/deviceId/{deviceId:^[1-9]\\d*$}/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResult getMaxHistoryDataWithDeviceIdAndDataStream(@PathVariable(name = "deviceId") Integer deviceId,
                                                                 @PathVariable(name = "dataStream") String dataStream,
                                                                 @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                 @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(!MyNumber.isPositive(deviceId) || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)) {
                return DataResult.fail("错误的设备id");
            }
            String dataStreamId = null;
            if(OneNetKey.isDataStreamId(dataStream)){ // 为资源id
                dataStreamId = dataStream;
            }else { // 可能为资源名称
                var tmp = this.lampService.getDataStreamIdByResourceName(dataStream);
                if(!MyString.isEmptyOrNull(tmp)){
                    dataStreamId = tmp;
                }
            }
            if(dataStreamId == null) return DataResult.fail("获取数据失败");

            var start = MyDateTime.toLocalDateTime(beginTimestamp);
            var end = MyDateTime.toLocalDateTime(endTimestamp);
            var ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, start, end, null, null, null);
            if(ret != null && ret.isOk()){
                Supplier<Stream<Double>> streamSupplier = () -> ret.data.dataStreams.stream().map(s -> {
                    var p = s.dataPoints.get(0);
                    if(p == null) return null;
                    return MyNumber.getDouble(p.value);
                });
                var max = streamSupplier.get().filter(Objects::nonNull).max(Comparator.comparing(Double::valueOf));
                return max.map(a -> DataResult.success(MyNumber.toString(a))).orElseGet(() -> DataResult.fail("获取数据为空"));
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个区域的某个时间段内的多个资源的最小值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionsAndDataStreams", value = "区域id集合或名字集合与资源id或名字集合", required = true, dataType = "Object", paramType = "body"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "Long", paramType = "path")
    })
    @PostMapping(value = "data/min/regions/dataStreams/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResultsMap getMinHistoryDataWithRegionsAndDataStreams(@RequestBody RegionsAndDataStreams regionsAndDataStreams,
                                                                     @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                     @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(regionsAndDataStreams == null || !regionsAndDataStreams.isValid() || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResultsMap.fail("错误的参数");
            }
            var result = new HashMap<String, Map<String, String>>();
            regionsAndDataStreams.regions.forEach(region -> {
                var map = new HashMap<String, String>();
                regionsAndDataStreams.dataStreams.forEach(dataStream -> {
                    if(!MyString.isEmptyOrNull(region) && !MyString.isEmptyOrNull(dataStream)){
                        var tmp = this.getMinHistoryDataWithRegionAndDataStream(region, dataStream, beginTimestamp, endTimestamp, request);
                        if(tmp != null && tmp.isOk()){
                            map.put(dataStream, tmp.value);
                        }
                    }
                });
                if(!map.isEmpty()){
                    result.put(region, map);
                }
            });
            if(!result.isEmpty()) return DataResultsMap.success(result);
            return DataResultsMap.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResultsMap.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个设备的某个时间段内的多个资源的最小值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIdsAndDataStreams", value = "设备id集合或名字集合与资源id或名字集合", required = true, dataType = "Object", paramType = "body"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "Long", paramType = "path")
    })
    @PostMapping(value = "data/min/deviceIds/dataStreams/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResultsMap getMinHistoryDataWithDeviceIdsAndDataStreams(@RequestBody DeviceIdsAndDataStreams deviceIdsAndDataStreams,
                                                                       @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                       @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(deviceIdsAndDataStreams == null || !deviceIdsAndDataStreams.isValid() || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResultsMap.fail("错误的参数");
            }
            var result = new HashMap<String, Map<String, String>>();
            deviceIdsAndDataStreams.deviceIds.forEach(deviceId -> {
                var map = new HashMap<String, String>();
                deviceIdsAndDataStreams.dataStreams.forEach(dataStream -> {
                    if(MyNumber.isPositive(deviceId) && !MyString.isEmptyOrNull(dataStream)){
                        var tmp = this.getMinHistoryDataWithDeviceIdAndDataStream(deviceId, dataStream, beginTimestamp, endTimestamp, request);
                        if(tmp != null && tmp.isOk()){
                            map.put(dataStream, tmp.value);
                        }
                    }
                });
                if(!map.isEmpty()){
                    result.put(deviceId.toString(), map);
                }
            });
            if(!result.isEmpty()) return DataResultsMap.success(result);
            return DataResultsMap.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResultsMap.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个区域的某个时间段内的某个资源的最小值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regions", value = "区域id集合或名字集合", required = true, dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "dataStream", value = "资源id或名字", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "Long", paramType = "path")
    })
    @PostMapping(value = "data/min/regions/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResults getMinHistoryDataWithRegionsAndDataStream(@RequestBody List<String> regions,
                                                                 @PathVariable(name = "dataStream") String dataStream,
                                                                 @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                 @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(regions == null || regions.isEmpty() || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResults.fail("错误的参数");
            }
            var map = new HashMap<String, String>();
            regions.forEach(region -> {
                if(!MyString.isEmptyOrNull(region)){
                    var tmp = this.getMinHistoryDataWithRegionAndDataStream(region, dataStream, beginTimestamp, endTimestamp, request);
                    if(tmp != null && tmp.isOk()){
                        map.put(region, tmp.value);
                    }
                }
            });
            if(!map.isEmpty()) return DataResults.success(map);
            return DataResults.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResults.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个设备的某个时间段内的某个资源的最小值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIds", value = "设备的id集合", required = true, dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "dataStream", value = "资源id或名字", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "Long", paramType = "path")
    })
    @PostMapping(value = "data/min/deviceIds/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResults getMinHistoryDataWithDeviceIdsAndDataStream(@RequestBody List<Integer> deviceIds,
                                                                   @PathVariable(name = "dataStream") String dataStream,
                                                                   @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                   @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(deviceIds == null || deviceIds.isEmpty() || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResults.fail("错误的参数");
            }
            String dataStreamId = null;
            if(OneNetKey.isDataStreamId(dataStream)){ // 为资源id
                dataStreamId = dataStream;
            }else { // 可能为资源名称
                var tmp = this.lampService.getDataStreamIdByResourceName(dataStream);
                if(!MyString.isEmptyOrNull(tmp)){
                    dataStreamId = tmp;
                }
            }
            if(dataStreamId == null) return DataResults.fail("获取数据失败");
            var map = new HashMap<String, String>();
            deviceIds.forEach(deviceId -> {
                if(MyNumber.isPositive(deviceId)){
                    var tmp = this.getAvgHistoryDataWithDeviceIdAndDataStream(deviceId, dataStream, beginTimestamp, endTimestamp, request);
                    if(tmp != null && tmp.isOk()) map.put(deviceId.toString(), tmp.value);
                }

            });
            if(!map.isEmpty()) return DataResults.success(map);
            return DataResults.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResults.fail("获取数据失败");
    }

    @ApiOperation(value = "获取某个区域的某个时间段内的某个资源的最小值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "region", value = "区域id或者名称", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStream", value = "资源id或者名称", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/min/region/{region}/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResult getMinHistoryDataWithRegionAndDataStream(@PathVariable(name = "region") String region,
                                                               @PathVariable(name = "dataStream") String dataStream,
                                                               @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                               @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(MyString.isEmptyOrNull(region) || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                return DataResult.fail("错误的参数");
            }
            String regionId = null;
            if(MyString.isUuid(region)){ // 为区域id
                regionId = region;
            }else { // 可能是区域名字
                var regionModel = this.regionService.getRegionByName(region);
                if(regionModel != null) regionId = regionModel.id;
            }
            String dataStreamId = null;
            if(OneNetKey.isDataStreamId(dataStream)){ // 为资源id
                dataStreamId = dataStream;
            }else { // 可能为资源名称
                var tmp = this.lampService.getDataStreamIdByResourceName(dataStream);
                if(!MyString.isEmptyOrNull(tmp)){
                    dataStreamId = tmp;
                }
            }
            if(regionId == null || dataStreamId == null) return DataResult.fail("获取数据失败");

            var lamps = this.lampService.getLampsByRegionId(regionId);
            if(lamps == null || lamps.isEmpty()){
                return DataResult.fail("获取数据失败");
            }
            var result = new ArrayList<Double>();
            for(var lamp: lamps){
                var ret = this.getMinHistoryDataWithDeviceIdAndDataStream(lamp.deviceId, dataStreamId, beginTimestamp, endTimestamp, request);
                if(ret != null && ret.isOk()){
                    var d = MyNumber.getDouble(ret.value);
                    if(d != null) {
                        result.add(d);
                    }
                }
            }
            var min = result.stream().min(Comparator.comparing(Double::valueOf)).orElse(null);
            if(min != null) return DataResult.success(MyNumber.toString(min));
            return DataResult.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取某个设备的某个时间段内的某个资源的最小值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "dataStream", value = "资源id或名称", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "Long", paramType = "path")
    })
    @GetMapping(value = "data/min/deviceId/{deviceId:^[1-9]\\d*$}/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResult getMinHistoryDataWithDeviceIdAndDataStream(@PathVariable(name = "deviceId") Integer deviceId,
                                                                 @PathVariable(name = "dataStream") String dataStream,
                                                                 @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                 @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(!MyNumber.isPositive(deviceId) || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)) {
                return DataResult.fail("错误的参数");
            }
            String dataStreamId = null;
            if(OneNetKey.isDataStreamId(dataStream)){ // 为资源id
                dataStreamId = dataStream;
            }else { // 可能为资源名称
                var tmp = this.lampService.getDataStreamIdByResourceName(dataStream);
                if(!MyString.isEmptyOrNull(tmp)){
                    dataStreamId = tmp;
                }
            }
            if(dataStreamId == null) return DataResult.fail("获取数据失败");

            var start = MyDateTime.toLocalDateTime(beginTimestamp);
            var end = MyDateTime.toLocalDateTime(endTimestamp);
            var ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, start, end, null, null, null);
            if(ret != null && ret.isOk()){
                Supplier<Stream<Double>> streamSupplier = () -> ret.data.dataStreams.stream().map(s -> {
                    var p = s.dataPoints.get(0);
                    if(p == null) return null;
                    return MyNumber.getDouble(p.value);
                });
                var min = streamSupplier.get().filter(Objects::nonNull).min(Comparator.comparing(Double::valueOf));
                return min.map(a -> DataResult.success(MyNumber.toString(a))).orElseGet(() -> DataResult.fail("获取数据为空"));
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个设备的多个资源的最新数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIdsAndDataStreams", value = "设备的Id集合与", required = true, dataType = "List", paramType = "body")
    })
    @PostMapping(value = "data/last/deviceIds/dataStreams", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public LastDataResultsMap getLastDataWithDeviceIdsAndDataStreams(@RequestBody DeviceIdsAndDataStreams deviceIdsAndDataStreams, HttpServletRequest request) {
        try{
            if(deviceIdsAndDataStreams == null || !deviceIdsAndDataStreams.isValid()){
                return LastDataResultsMap.fail("错误的参数");
            }
            var result = new HashMap<String, Map<String, LastData>>();
            deviceIdsAndDataStreams.deviceIds.forEach(deviceId -> {
                var map = new HashMap<String, LastData>();
                deviceIdsAndDataStreams.dataStreams.forEach(dataStream -> {
                    if(MyNumber.isPositive(deviceId) && !MyString.isEmptyOrNull(dataStream)){
                        var tmp = this.getLastDataWithDeviceIdAndDataStream(deviceId, dataStream, request);
                        if(tmp != null && tmp.isOk()){
                            var lastData = new LastData();
                            lastData.updateAt = tmp.updateAt;
                            lastData.currentValue = tmp.currentValue;
                            map.put(dataStream, lastData);
                        }
                    }
                });
                if(!map.isEmpty()){
                    result.put(deviceId.toString(), map);
                }
            });
            if(!result.isEmpty()) return LastDataResultsMap.success(result);
            return LastDataResultsMap.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return LastDataResultsMap.fail("获取数据失败");
    }

    @ApiOperation(value = "获取多个设备的某个资源的最新数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIds", value = "设备的Id集合", required = true, dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "dataStream", value = "资源Id或名称", required = true, dataType = "String", paramType = "path")
    })
    @PostMapping(value = "data/last/deviceIds/dataStream/{dataStream}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public LastDataResults getLastDataWithDeviceIdAndDataStreams(@RequestBody List<Integer> deviceIds,
                                                                 @PathVariable(name = "dataStream") String dataStream, HttpServletRequest request) {
        try{
            if(deviceIds == null || deviceIds.isEmpty() || MyString.isEmptyOrNull(dataStream)){
                return LastDataResults.fail("错误的参数");
            }
            var map = new HashMap<String, LastData>();
            deviceIds.forEach(deviceId -> {
                if(MyNumber.isPositive(deviceId)){
                    var tmp = this.getLastDataWithDeviceIdAndDataStream(deviceId, dataStream, request);
                    if(tmp != null && tmp.isOk()){
                        var lastData = new LastData();
                        lastData.updateAt = tmp.updateAt;
                        lastData.currentValue = tmp.currentValue;
                        map.put(deviceId.toString(), lastData);
                    }
                }
            });
            if(!map.isEmpty()) return LastDataResults.success(map);
            return LastDataResults.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return LastDataResults.fail("获取数据失败");
    }

    @ApiOperation(value = "获取设备的多个资源的最新数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStreams", value = "资源Id或名称集合", required = true, dataType = "List", paramType = "body")
    })
    @PostMapping(value = "data/last/deviceId/{deviceId:^[1-9]\\d*$}/dataStreams", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public LastDataResults getLastDataWithDeviceIdAndDataStreams(@PathVariable(name = "deviceId") Integer deviceId,
                                                                 @RequestBody List<String> dataStreams, HttpServletRequest request) {
        try{
            if(!MyNumber.isPositive(deviceId) || dataStreams == null || dataStreams.isEmpty()){
                return LastDataResults.fail("错误的参数");
            }
            var map = new HashMap<String, LastData>();
            dataStreams.forEach(dataStream -> {
                if(!MyString.isEmptyOrNull(dataStream)){
                    var tmp = this.getLastDataWithDeviceIdAndDataStream(deviceId, dataStream, request);
                    if(tmp != null && tmp.isOk()){
                        var lastData = new LastData();
                        lastData.updateAt = tmp.updateAt;
                        lastData.currentValue = tmp.currentValue;
                        map.put(dataStream, lastData);
                    }
                }
            });
            if(!map.isEmpty()) return LastDataResults.success(map);
            return LastDataResults.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return LastDataResults.fail("获取数据失败");
    }

    @ApiOperation(value = "获取设备的最新数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStream", value = "资源Id或名称", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "data/last/deviceId/{deviceId:^[1-9]\\d*$}/dataStream/{dataStream}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public LastDataResult getLastDataWithDeviceIdAndDataStream(@PathVariable(name = "deviceId") Integer deviceId,
                                                               @PathVariable(name = "dataStream") String dataStream, HttpServletRequest request) {
        try {
            if(!MyNumber.isPositive(deviceId) || MyString.isEmptyOrNull(dataStream)){
                return LastDataResult.fail("错误的参数");
            }
            String dataStreamId = null;
            if(OneNetKey.isDataStreamId(dataStream)){ // 为资源id
                dataStreamId = dataStream;
            }else { // 可能为资源名称
                var tmp = this.lampService.getDataStreamIdByResourceName(dataStream);
                if(!MyString.isEmptyOrNull(tmp)){
                    dataStreamId = tmp;
                }
            }
            if(dataStreamId == null) return LastDataResult.fail("未知资源");

            var oneNetKey = OneNetKey.from(dataStreamId);
            if(oneNetKey == null) return LastDataResult.fail("非法的参数");

            if(!this.lampService.isDataStreamIdEnabled(deviceId, oneNetKey)){
                return LastDataResult.fail("当前资源已禁用");
            }
            var ret = this.oneNet.getLastDataStreamById(deviceId, dataStreamId);
            if(ret != null && ret.isOk()){
                return LastDataResult.success(ret);
            }
            return LastDataResult.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return LastDataResult.fail("获取数据失败");
    }

    private double modifyLocation(String value){
        var idx = value.indexOf(".");
        var location = 0.0;
        if(idx != -1){
            var aft = Double.parseDouble(value.substring(idx-2));
            var pre = Double.parseDouble(value.substring(0, idx-2));
            location = pre+aft/60;
        }
        return location;
    }

    @ApiOperation(value = "获取设备的历史数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStream", value = "资源Id或名称", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/history/deviceId/{deviceId:^[1-9]\\d*$}/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public HistoryDataResult getHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                            @PathVariable(name = "dataStream") String dataStream,
                                            @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                            @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(!MyNumber.isPositive(deviceId) || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp)){
                HistoryDataResult.fail("错误的参数");
            }
            String dataStreamId = null;
            if(OneNetKey.isDataStreamId(dataStream)){ // 为资源id
                dataStreamId = dataStream;
            }else { // 可能为资源名称
                var tmp = this.lampService.getDataStreamIdByResourceName(dataStream);
                if(!MyString.isEmptyOrNull(tmp)){
                    dataStreamId = tmp;
                }
            }
            if(dataStreamId == null) return HistoryDataResult.fail("未知资源");

            var begin = MyDateTime.toLocalDateTime(beginTimestamp);
            var end = MyDateTime.toLocalDateTime(endTimestamp);
            var ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, null, null, null);
            if(ret != null && ret.isOk()){
                return HistoryDataResult.success(ret);
            }
            return HistoryDataResult.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return HistoryDataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取设备的历史数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStream", value = "资源Id或名称", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "limit", value = "数量", required = true, dataType = "int", paramType = "path")
    })
    @GetMapping(value = "data/history/deviceId/{deviceId:^[1-9]\\d*$}/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}/{limit:^[1-9]\\d*$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public HistoryDataResult getHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                            @PathVariable(name = "dataStream") String dataStream,
                                            @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                            @PathVariable(name = "endTimestamp") Long endTimestamp,
                                            @PathVariable(name = "limit") Integer limit, HttpServletRequest request) {
        try{
            if(!MyNumber.isPositive(deviceId) || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp) || !MyNumber.isPositive(limit)){
                return HistoryDataResult.fail("错误的参数");
            }
            var begin = MyDateTime.toLocalDateTime(beginTimestamp);
            var end = MyDateTime.toLocalDateTime(endTimestamp);
            String dataStreamId = null;
            if(OneNetKey.isDataStreamId(dataStream)){ // 为资源id
                dataStreamId = dataStream;
            }else { // 可能为资源名称
                var tmp = this.lampService.getDataStreamIdByResourceName(dataStream);
                if(!MyString.isEmptyOrNull(tmp)){
                    dataStreamId = tmp;
                }
            }
            if(dataStreamId == null) return HistoryDataResult.fail("未知资源");
            var ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, limit, null, null);
            if(ret != null && ret.isOk()){
                return HistoryDataResult.success(ret);
            }
            return HistoryDataResult.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return HistoryDataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取设备的历史数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStream", value = "资源Id或名称", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "limit", value = "数量", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "sort", value = "排序规则，升序ASC，降序DESC", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "data/history/deviceId/{deviceId:^[1-9]\\d*$}/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}/{limit:^[1-9]\\d*$}/{sort:DESC|ASC}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public HistoryDataResult getHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                            @PathVariable(name = "dataStream") String dataStream,
                                            @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                            @PathVariable(name = "endTimestamp") Long endTimestamp,
                                            @PathVariable(name = "limit") Integer limit,
                                            @PathVariable(name = "sort") String sort, HttpServletRequest request) {
        try{
            if(!MyNumber.isPositive(deviceId) || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp) || !MyNumber.isPositive(limit)){
                return HistoryDataResult.fail("错误的参数");
            }
            String dataStreamId = null;
            if(OneNetKey.isDataStreamId(dataStream)){ // 为资源id
                dataStreamId = dataStream;
            }else { // 可能为资源名称
                var tmp = this.lampService.getDataStreamIdByResourceName(dataStream);
                if(!MyString.isEmptyOrNull(tmp)){
                    dataStreamId = tmp;
                }
            }
            if(dataStreamId == null) return HistoryDataResult.fail("未知资源");
            var begin = MyDateTime.toLocalDateTime(beginTimestamp);
            var end = MyDateTime.toLocalDateTime(endTimestamp);
            var ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, limit, sort, null);
            if(ret != null && ret.isOk()){
                return HistoryDataResult.success(ret);
            }
            return HistoryDataResult.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return HistoryDataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取设备的历史数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStream", value = "资源Id或名称", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "limit", value = "数量", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "cursor", value = "从当前cursor开始", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "sort", value = "排序规则，升序ASC，降序DESC", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "data/history/deviceId/{deviceId:^[1-9]\\d*$}/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}/{limit:^[1-9]\\d*$}/{cursor:^\\d+_\\d+_\\d+$}/{sort:DESC|ASC}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public HistoryDataResult getHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                            @PathVariable(name = "dataStream") String dataStream,
                                            @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                            @PathVariable(name = "endTimestamp") Long endTimestamp,
                                            @PathVariable(name = "limit") Integer limit,
                                            @PathVariable(name = "cursor") String cursor,
                                            @PathVariable(name = "sort") String sort, HttpServletRequest request) {
        try{
            if(!MyNumber.isPositive(deviceId) || MyString.isEmptyOrNull(dataStream) || !MyDateTime.isAllPastAndStrict(beginTimestamp, endTimestamp) || !MyNumber.isPositive(limit)){
                HistoryDataResult.fail("错误的参数");
            }
            String dataStreamId = null;
            if(OneNetKey.isDataStreamId(dataStream)){ // 为资源id
                dataStreamId = dataStream;
            }else { // 可能为资源名称
                var tmp = this.lampService.getDataStreamIdByResourceName(dataStream);
                if(!MyString.isEmptyOrNull(tmp)){
                    dataStreamId = tmp;
                }
            }
            if(dataStreamId == null) return HistoryDataResult.fail("未知资源");
            var begin = MyDateTime.toLocalDateTime(beginTimestamp);
            var end = MyDateTime.toLocalDateTime(endTimestamp);
            var ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, limit, sort, cursor);
            if(ret != null && ret.isOk()){
                return HistoryDataResult.success(ret);
            }
            return HistoryDataResult.fail("获取数据为空");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return HistoryDataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "针对某一设备执行某个命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path")
    })
    @PostMapping(value = "execute/{deviceId:^[1-9]\\d*$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public ExecuteResult execute(@PathVariable("deviceId") Integer deviceId,
                                 @ApiParam(value = "具体的命令", required = true) @RequestBody OneNetExecuteArgs data,
                                 HttpServletRequest request){
        try{
            if(!MyNumber.isPositive(deviceId) || !data.isValid()){
                return ExecuteResult.fail("非法的参数");
            }
            var resource = this.lampService.getResourceByCommandValue(data.cmd);
            if(resource == null) {
                return ExecuteResult.fail("获取命令相关数据失败");
            }

            var cmd = data.getPackedCmd();
            var imei = this.lampService.getImeiByDeviceId(deviceId);
            if(MyString.isEmptyOrNull(imei)){
                return ExecuteResult.fail("未找到指定设备的imei");
            }
            var params = new CommandParams();
            params.deviceId = deviceId;
            params.imei = imei;
            params.oneNetKey = resource.toOneNetKey();
            params.timeout = resource.timeout;
            params.command = cmd;
            var ret = oneNet.execute(params);

            if(ret.isOk()) {
                return ExecuteResult.success(ret);
            }
            else {
                log.debug(ret.error);
            }

        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return ExecuteResult.fail("执行命令发生错误");
    }

    @ApiOperation(value = "获取所有的设备Id和名字")
    //@PreAuthorize("hasAuthority('USER') and isRequestAllowed(#request)")
    @GetMapping(value = "info", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    @Override
    public DeviceIdAndNameResult getDeviceIdAndName(@RequestHeader("userId") String userId, HttpServletRequest request){
        try{
            //var principal = request.getUserPrincipal(); // 这个传来的是token中的用户名，不是登入用户名
            //var userName = principal.getName();
            //var user = this.userService.getUserByName(userName);
            if(!MyString.isUuid(userId)){
                return DeviceIdAndNameResult.fail("非法的参数");
            }
            var user = this.userService.getUserById(userId);
            var tmp = this.lampService.getDeviceIdAndDeviceNames(user.organizationId);
            if(tmp == null || tmp.isEmpty()){
                return DeviceIdAndNameResult.fail("获取数据为空");
            }
            return DeviceIdAndNameResult.success(tmp);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DeviceIdAndNameResult.fail("获取数据失败");
    }

    @ApiOperation(value = "新建灯带效果")
    @PostMapping(value = "ministar", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public ExecuteResult newMiniStar(@ApiParam(value = "具体的指令集", required = true) @RequestBody List<OneNetExecuteArgs> data, HttpServletRequest request) {
        try{
            if(data == null || data.isEmpty()) return ExecuteResult.fail("非法的参数");
            for(var arg : data){
                if(!arg.isValid()) {
                    return ExecuteResult.fail("非法的参数");
                }
                var subtitle = arg.getSubtitle();
                var user = this.userService.getUserById(subtitle.userId);
                if(user == null) {
                    return ExecuteResult.fail("指定的用户不存在");
                }
                var region = this.regionService.getRegionById(subtitle.regionId);
                if(region == null) {
                    return ExecuteResult.fail("指定的区域不存在");
                }
                var resource = this.lampService.getResourceByCommandValue(OneNetService.ZNLD_DD_EXECUTE);
                if(resource == null) {
                    return ExecuteResult.fail("获取命令相关数据失败");
                }
                var lamps = this.lampService.getLampsByRegionId(subtitle.regionId);
                if(lamps == null || lamps.isEmpty()) {
                    return ExecuteResult.fail("获取该区域下的路灯为空");
                }
                arg.cmd = OneNetService.ZNLD_DD_EXECUTE;
                for (var lamp : lamps) {
                    var ret = this.execute(lamp.deviceId, arg, request);
                    if (!ret.isOk()) {
                        log.debug(ret.msg);
                        return ExecuteResult.fail("上传效果失败");
                    }
                }
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ExecuteResult.fail("上传效果失败");
        }
        return ExecuteResult.success("上传效果成功");
    }
    public void getWeightedData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp){
        var begin = MyDateTime.toLocalDateTime(beginTimestamp);
        var end = MyDateTime.toLocalDateTime(endTimestamp);
        var ret = this.oneNet.getWeightedData(deviceId, dataStreamId, begin, end);
    }
}
