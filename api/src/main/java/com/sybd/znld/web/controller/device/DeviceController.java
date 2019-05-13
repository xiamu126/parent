package com.sybd.znld.web.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.model.lamp.dto.DeviceIdsAndDataStreams;
import com.sybd.znld.model.lamp.dto.RegionsAndDataStreams;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.lamp.IRegionService;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.web.controller.device.dto.*;
import com.sybd.znld.web.onenet.IOneNetService;
import com.sybd.znld.web.onenet.OneNetService;
import com.sybd.znld.web.onenet.dto.CommandParams;
import com.sybd.znld.web.onenet.dto.OneNetExecuteArgs;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.znld.util.MyDateTime;
import com.sybd.znld.znld.util.MyNumber;
import com.sybd.znld.znld.util.MyString;
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

    @ApiOperation(value = "获取设备的某个资源历史数据，仅指定开始时间，结束时间为当前时间")
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
            @ApiImplicitParam(name = "regionsAndDataStreams", value = "区域id集合或名字集合与数据流Id集合或具体的资源名称集合", required = true, dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/avg/regions/dataStreams/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResultsMap getAvgHistoryData(@RequestBody RegionsAndDataStreams regionsAndDataStreams,
                                            @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                            @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        if(regionsAndDataStreams == null || regionsAndDataStreams.regions == null || regionsAndDataStreams.dataStreams == null){
            return DataResultsMap.fail("获取数据失败");
        }
        var result = new HashMap<String, Map<String, String>>();
        regionsAndDataStreams.regions.forEach(region -> {
            String regionId = null;
            if(!MyString.isEmptyOrNull(region) && !MyString.isUuid(region)){ // 传入的可能是区域名字，尝试从名字获取id
                var regionModel = this.regionService.getRegionByName(region);
                if(regionModel != null) {
                    regionId = regionModel.id;
                }
            }else if(MyString.isUuid(region)) { // 如果是区域id
                regionId = region;
            }
            final String theRegionId = regionId;
            var map = new HashMap<String, String>();
            regionsAndDataStreams.dataStreams.forEach(dataStream -> {
                String dataStreamId = null;
                if(!MyString.isEmptyOrNull(dataStream) && !OneNetKey.isDataStreamId(dataStream)) { // 传入的可能是资源名称
                    dataStreamId = this.lampService.getDataStreamIdByResourceName(dataStream); // 尝试通过此名称获取资源id
                }else if(OneNetKey.isDataStreamId(dataStream)){ // 如果是资源id
                    dataStreamId = dataStream;
                }
                if(theRegionId != null && dataStreamId != null){
                    var tmp = this.getAvgHistoryData(theRegionId, dataStreamId, beginTimestamp, endTimestamp, request);
                    if(tmp != null && tmp.isOk()) map.put(dataStream, tmp.value);
                }
            });
            if(map.size() > 0){
                result.put(region, map);
            }
        });
        if(result.size() > 0) return DataResultsMap.success(result);
        return DataResultsMap.fail("获取数据失败");
    }

    @ApiOperation(value = "获取某个区域的某时间段内的多个资源的各自的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "region", value = "区域id或名字", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "dataStreams", value = "查看的数据流Id集合或具体的资源名称集合", required = true, dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/avg/region/{region}/dataStreams/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResults getAvgHistoryData(@PathVariable(name = "region") String region,
                                         @RequestBody List<String> dataStreams,
                                         @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                         @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        var map = new HashMap<String, String>();
        String regionId = null;
        if(!MyString.isEmptyOrNull(region) && !MyString.isUuid(region)){ // 传入的可能是区域名字，尝试从名字获取id
            var regionModel = this.regionService.getRegionByName(region);
            if(regionModel != null) {
                regionId = regionModel.id;
            }
        }else if(MyString.isUuid(region)) { // 如果是区域id
            regionId = region;
        }
        final String theRegionId = regionId;
        if(dataStreams == null){
            return DataResults.fail("获取失败");
        }
        dataStreams.forEach(dataStream -> {
            String dataStreamId = null;
            if(!MyString.isEmptyOrNull(dataStream) && !OneNetKey.isDataStreamId(dataStream)) { // 传入的可能是资源名称
                dataStreamId = this.lampService.getDataStreamIdByResourceName(dataStream); // 尝试通过此名称获取资源id
            }else if(OneNetKey.isDataStreamId(dataStream)){ // 如果是资源id
                dataStreamId = dataStream;
            }
            if(theRegionId != null && dataStreamId != null){
                var tmp = this.getAvgHistoryData(theRegionId, dataStreamId, beginTimestamp, endTimestamp, request);
                if(tmp != null && tmp.isOk()) map.put(dataStream, tmp.value);
            }
        });
        if(map.size() > 0) return DataResults.success(map);
        return DataResults.fail("获取失败");
    }

    @ApiOperation(value = "获取多个区域的某时间段内的某一资源的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regions", value = "区域的id集合或具体的名称集合", required = true, dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "dataStream", value = "查看的数据流Id或资源名称", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/avg/regions/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResults getAvgHistoryData(@RequestBody List<String> regions,
                                         @PathVariable(name = "dataStream") String dataStream,
                                         @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                         @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        var map = new HashMap<String, String>();
        String dataStreamId = null;
        if(!MyString.isEmptyOrNull(dataStream) && !OneNetKey.isDataStreamId(dataStream)) { // 传入的可能是资源名称
            dataStreamId = this.lampService.getDataStreamIdByResourceName(dataStream); // 尝试通过此名称获取资源id
        }else if(OneNetKey.isDataStreamId(dataStream)){ // 如果是资源id
            dataStreamId = dataStream;
        }
        final String theDataStreamId = dataStreamId;
        if(regions == null){
            return DataResults.fail("获取失败");
        }
        regions.forEach(region -> {
            String regionId = null;
            if(MyString.isUuid(region)){ // 如果传入的是区域id
               regionId = region;
            }else if(!MyString.isEmptyOrNull(region) && !MyString.isUuid(region)){ // 传入的可能是区域名字，尝试从名字获取id
                var regionModel = this.regionService.getRegionByName(region);
                if(regionModel != null) {
                    regionId = regionModel.id;
                }
            }
            if(regionId != null && theDataStreamId != null){
                var tmp = this.getAvgHistoryData(regionId, theDataStreamId, beginTimestamp, endTimestamp, request);
                if(tmp != null && tmp.isOk()) map.put(region, tmp.value);
            }
        });
        if(map.size() > 0) return DataResults.success(map);
        return DataResults.fail("获取失败");
    }

    @Override
    public DataResultsMap getAvgHistoryDataWithDeviceIdsAndDataStreams(DeviceIdsAndDataStreams deviceIdsAndDataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request) {
        return null;
    }

    @Override
    public DataResults getAvgHistoryDataWithDeviceIdAndDataStreams(Integer deviceId, List<String> dataStreams, Long beginTimestamp, Long endTimestamp, HttpServletRequest request) {
        return null;
    }

    @ApiOperation(value = "获取多个设备的某时间段内的某一资源的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIds", value = "设备id集合", required = true, dataType = "List", paramType = "body"),
            @ApiImplicitParam(name = "dataStream", value = "查看的数据流Id或资源名称", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/avg/deviceIds/dataStream/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResults getAvgHistoryDataWithDeviceIdsAndDataStream(@RequestBody List<Integer> deviceIds,
                                                                   @PathVariable(name = "dataStream") String dataStream,
                                                                   @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                                   @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        if(deviceIds == null || deviceIds.isEmpty() || MyString.isEmptyOrNull(dataStream) || MyDateTime.isAllPastAndStrictDesc(beginTimestamp, endTimestamp)){
            return DataResults.fail("参数错误");
        }
        String dataStreamId = null;
        if(!MyString.isEmptyOrNull(dataStream) && !OneNetKey.isDataStreamId(dataStream)) { // 传入的可能是资源名称
            dataStreamId = this.lampService.getDataStreamIdByResourceName(dataStream); // 尝试通过此名称获取资源id
        }else if(OneNetKey.isDataStreamId(dataStream)){ // 如果是资源id
            dataStreamId = dataStream;
        }
        final String theDataStreamId = dataStreamId;
        var map = new HashMap<String, String>();
        deviceIds.forEach(deviceId -> {
            if(MyNumber.isPositive(deviceId)){
                var tmp = this.getAvgHistoryData(deviceId, theDataStreamId, beginTimestamp, endTimestamp, request);
                if(tmp != null && tmp.isOk()){
                    map.put(deviceId.toString(), tmp.value);
                }
            }
        });
        if(!map.isEmpty()) return DataResults.success(map);
        return DataResults.fail("获取数据失败");
    }

    @ApiOperation(value = "获取区域的某个时间段内的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionId", value = "区域Id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/avg/{regionId:[0-9a-f]{32}}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResult getAvgHistoryData(@PathVariable(name = "regionId") String regionId,
                                        @PathVariable(name = "dataStreamId") String dataStreamId,
                                        @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                        @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(!MyString.isUuid(regionId)){
                return DataResult.fail("错误的参数");
            }
            if(OneNetKey.from(dataStreamId) == null){
                return DataResult.fail("错误的参数");
            }
            if(!MyDateTime.isAllPast(beginTimestamp, endTimestamp) || beginTimestamp > endTimestamp){
                return DataResult.fail("错误的参数");
            }
            var lamps = this.lampService.getLampsByRegionId(regionId);
            if(lamps == null || lamps.isEmpty()){
                return DataResult.fail("获取数据失败");
            }
            var sum = 0.0;
            var count = 0;
            for(var lamp: lamps){
                var ret = this.getAvgHistoryData(lamp.deviceId, dataStreamId, beginTimestamp, endTimestamp, request);
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
            return DataResult.fail("获取数据失败");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取设备的某个时间段内的平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/avg/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResult getAvgHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                        @PathVariable(name = "dataStreamId") String dataStreamId,
                                        @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                        @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(deviceId == null || MyNumber.isNegativeOrZero(deviceId)) {
                return DataResult.fail("错误的参数");
            }
            if(OneNetKey.from(dataStreamId) == null){
                return DataResult.fail("错误的参数");
            }
            if(!MyDateTime.isAllPast(beginTimestamp, endTimestamp) || beginTimestamp > endTimestamp){
                return DataResult.fail("错误的参数");
            }
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
                return DataResult.fail("获取数据失败");
            }
            return DataResult.fail("获取数据失败");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取区域的某个时间段内的最大值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/max/{regionId:[0-9a-f]{32}}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResult getMaxHistoryData(@PathVariable(name = "regionId") String regionId,
                                        @PathVariable(name = "dataStreamId") String dataStreamId,
                                        @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                        @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(!MyString.isUuid(regionId)){
                return DataResult.fail("错误的区域id");
            }
            if(OneNetKey.from(dataStreamId) == null){
                return DataResult.fail("错误的资源id");
            }
            if(!MyDateTime.isAllPast(beginTimestamp, endTimestamp) || beginTimestamp > endTimestamp){
                return DataResult.fail("错误的时间戳参数");
            }
            var lamps = this.lampService.getLampsByRegionId(regionId);
            if(lamps == null || lamps.isEmpty()){
                return DataResult.fail("获取数据失败");
            }
            var result = new ArrayList<Double>();
            for(var lamp: lamps){
                var ret = this.getMaxHistoryData(lamp.deviceId, dataStreamId, beginTimestamp, endTimestamp, request);
                if(ret != null && ret.isOk()){
                    var d = MyNumber.getDouble(ret.value);
                    if(d != null) {
                        result.add(d);
                    }
                }
            }
            var max = result.stream().max(Comparator.comparing(Double::valueOf)).orElse(null);
            if(max != null) return DataResult.success(MyNumber.toString(max));
            return DataResult.fail("获取数据失败");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取设备的某个时间段内的最大值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "Long", paramType = "path")
    })
    @GetMapping(value = "data/max/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResult getMaxHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                        @PathVariable(name = "dataStreamId") String dataStreamId,
                                        @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                        @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(deviceId == null || MyNumber.isNegativeOrZero(deviceId)) {
                return DataResult.fail("错误的设备id");
            }
            if(OneNetKey.from(dataStreamId) == null){
                return DataResult.fail("错误的资源id");
            }
            if(!MyDateTime.isAllPast(beginTimestamp, endTimestamp) || beginTimestamp > endTimestamp){
                return DataResult.fail("错误的时间戳");
            }
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
                return max.map(a -> DataResult.success(MyNumber.toString(a))).orElseGet(() -> DataResult.fail("获取数据失败"));
            }
            return DataResult.fail("获取数据失败");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取区域的某个时间段内的最小值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/min/{regionId:[0-9a-f]{32}}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResult getMinHistoryData(@PathVariable(name = "regionId") String regionId,
                                        @PathVariable(name = "dataStreamId") String dataStreamId,
                                        @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                        @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(!MyString.isUuid(regionId)){
                return DataResult.fail("错误的参数");
            }
            if(OneNetKey.from(dataStreamId) == null){
                return DataResult.fail("错误的参数");
            }
            if(!MyDateTime.isAllPast(beginTimestamp, endTimestamp) || beginTimestamp > endTimestamp){
                return DataResult.fail("错误的参数");
            }
            var lamps = this.lampService.getLampsByRegionId(regionId);
            if(lamps == null || lamps.isEmpty()){
                return DataResult.fail("获取数据失败");
            }
            var result = new ArrayList<Double>();
            for(var lamp: lamps){
                var ret = this.getMinHistoryData(lamp.deviceId, dataStreamId, beginTimestamp, endTimestamp, request);
                if(ret != null && ret.isOk()){
                    var d = MyNumber.getDouble(ret.value);
                    if(d != null) {
                        result.add(d);
                    }
                }
            }
            var min = result.stream().min(Comparator.comparing(Double::valueOf)).orElse(null);
            if(min != null) return DataResult.success(MyNumber.toString(min));
            return DataResult.fail("获取数据失败");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取设备的某个时间段内的最小值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "Long", paramType = "path")
    })
    @GetMapping(value = "data/min/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DataResult getMinHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                        @PathVariable(name = "dataStreamId") String dataStreamId,
                                        @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                        @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            if(deviceId == null || MyNumber.isNegativeOrZero(deviceId)) {
                return DataResult.fail("错误的参数");
            }
            if(OneNetKey.from(dataStreamId) == null){
                return DataResult.fail("错误的参数");
            }
            if(!MyDateTime.isAllPast(beginTimestamp, endTimestamp) || beginTimestamp > endTimestamp){
                return DataResult.fail("错误的参数");
            }
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
                return min.map(a -> DataResult.success(MyNumber.toString(a))).orElseGet(() -> DataResult.fail("获取数据失败"));
            }
            return DataResult.fail("获取数据失败");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return DataResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取设备的最新数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "data/last/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public LastDataResult getLastData(@PathVariable(name = "deviceId") Integer deviceId,
                                      @PathVariable(name = "dataStreamId") String dataStreamId, HttpServletRequest request) {
        try {
            var oneNetKey = OneNetKey.from(dataStreamId);
             if(oneNetKey == null) return LastDataResult.fail("非法的参数");
            if(!this.lampService.isDataStreamIdEnabled(deviceId, oneNetKey)){
                return LastDataResult.fail("当前dataStreamId已禁用");
            }
            var ret = this.oneNet.getLastDataStreamById(deviceId, dataStreamId);
            if(ret.isOk()){
                return LastDataResult.success(ret);
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return LastDataResult.fail("获取最新数据失败");
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
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/history/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public HistoryDataResult getHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                            @PathVariable(name = "dataStreamId") String dataStreamId,
                                            @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                            @PathVariable(name = "endTimestamp") Long endTimestamp, HttpServletRequest request) {
        try{
            var begin = MyDateTime.toLocalDateTime(beginTimestamp);
            var end = MyDateTime.toLocalDateTime(endTimestamp);
            if(begin.isAfter(end)){
                return HistoryDataResult.fail("参数错误");
            }
            var ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, null, null, null);
            if(ret.isOk()){
                return HistoryDataResult.success(ret);
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return HistoryDataResult.fail("获取历史数据失败");
    }

    @ApiOperation(value = "获取设备的历史数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "limit", value = "数量", required = true, dataType = "int", paramType = "path")
    })
    @GetMapping(value = "data/history/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}/{limit:^[1-9]\\d*$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public HistoryDataResult getHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                    @PathVariable(name = "dataStreamId") String dataStreamId,
                                    @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                    @PathVariable(name = "endTimestamp") Long endTimestamp,
                                    @PathVariable(name = "limit") Integer limit, HttpServletRequest request) {
        try{
            var begin = MyDateTime.toLocalDateTime(beginTimestamp);
            var end = MyDateTime.toLocalDateTime(endTimestamp);
            var ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, limit, null, null);
            if(ret.isOk()){
                return HistoryDataResult.success(ret);
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return HistoryDataResult.fail("获取历史数据失败");
    }

    @ApiOperation(value = "获取设备的历史数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "limit", value = "数量", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "sort", value = "排序规则，升序ASC，降序DESC", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "data/history/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}/{limit:^[1-9]\\d*$}/{sort:DESC|ASC}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public HistoryDataResult getHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                    @PathVariable(name = "dataStreamId") String dataStreamId,
                                    @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                    @PathVariable(name = "endTimestamp") Long endTimestamp,
                                    @PathVariable(name = "limit") Integer limit,
                                    @PathVariable(name = "sort") String sort, HttpServletRequest request) {
        try{
            var begin = MyDateTime.toLocalDateTime(beginTimestamp);
            var end = MyDateTime.toLocalDateTime(endTimestamp);
            var ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, limit, sort, null);
            if(ret.isOk()){
                return HistoryDataResult.success(ret);
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return HistoryDataResult.fail("获取历史数据失败");
    }

    @ApiOperation(value = "获取设备的历史数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "endTimestamp", value = "结束时间（时间戳）", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "limit", value = "数量", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "cursor", value = "从当前cursor开始", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "sort", value = "排序规则，升序ASC，降序DESC", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "data/history/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}/{limit:^[1-9]\\d*$}/{cursor:^\\d+_\\d+_\\d+$}/{sort:DESC|ASC}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public HistoryDataResult getHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                    @PathVariable(name = "dataStreamId") String dataStreamId,
                                    @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                    @PathVariable(name = "endTimestamp") Long endTimestamp,
                                    @PathVariable(name = "limit") Integer limit,
                                    @PathVariable(name = "cursor") String cursor,
                                    @PathVariable(name = "sort") String sort, HttpServletRequest request) {
        try{
            var begin = MyDateTime.toLocalDateTime(beginTimestamp);
            var end = MyDateTime.toLocalDateTime(endTimestamp);
            if(begin.isAfter(end) || limit <= 0){
                return HistoryDataResult.fail("参数错误");
            }
            var ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, limit, sort, cursor);
            if(ret.isOk()){
                return HistoryDataResult.success(ret);
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return HistoryDataResult.fail("获取历史数据失败");
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
