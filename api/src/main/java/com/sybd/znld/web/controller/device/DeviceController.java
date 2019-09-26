package com.sybd.znld.web.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.dto.DeviceIdsAndDataStreams;
import com.sybd.znld.model.lamp.dto.RegionsAndDataStreams;
import com.sybd.znld.model.ministar.dto.Subtitle;
import com.sybd.znld.model.onenet.Command;
import com.sybd.znld.model.onenet.dto.CommandParams;
import com.sybd.znld.model.onenet.dto.OneNetExecuteArgs;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.lamp.IRegionService;
import com.sybd.znld.service.ministar.IMiniStarService;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.web.controller.device.dto.*;
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
import java.util.stream.Collectors;
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
    private final IMiniStarService miniStarService;

    @Autowired
    public DeviceController(RedissonClient redissonClient,
                            IOneNetService oneNet,
                            ILampService lampService,
                            ProjectConfig projectConfig,
                            IUserService userService,
                            IRegionService regionService, IMiniStarService miniStarService) {
        this.redissonClient = redissonClient;
        this.oneNet = oneNet;
        this.lampService = lampService;
        this.projectConfig = projectConfig;
        this.userService = userService;
        this.regionService = regionService;
        this.miniStarService = miniStarService;
    }

    private Map<LocalDateTime, Double> getResourceByHour(Integer deviceId, String dataStreamId, LocalDateTime begin, LocalDateTime end){
        try{
            if(begin.isEqual(end)){
                log.debug("开始时间等于结束时间");
                return null;
            }
            var result = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, null, null, null);//过去24小时内的数据
            if(result == null || result.data == null || result.data.count == null || result.data.count <= 0){
                return null;
            }
            var data = (result.getData().getDataStreams().get(0)).getDataPoints();

            var tmp = data.stream().collect(Collectors.groupingBy(d -> {
                var time = LocalDateTime.parse(d.at, DateTimeFormatter.ofPattern(MyDateTime.format2));
                return LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), time.getHour(), 0, 0);
            }));

            var ret = new TreeMap<LocalDateTime, Double>();
            for(var e : tmp.entrySet()){
                var key = e.getKey();
                var value = e.getValue();
                var avg = value.stream().mapToDouble(t -> Double.parseDouble(t.value)).average().orElse(0);
                ret.put(key, avg);
            }

            return ret;
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
    @GetMapping(value = "data/history/pretty/{deviceId:^[1-9]\\d*$}/{dataStream}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public PrettyHistoryDataResult getPrettyHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                                        @PathVariable(name = "dataStream") String dataStream,
                                                        @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                                        @PathVariable(name = "endTimestamp") Long endTimestamp){
        String dataStreamId = null;
        if(OneNetKey.isDataStreamId(dataStream)){ // 为资源id
            dataStreamId = dataStream;
        }else { // 可能为资源名称
            var tmp = this.lampService.getDataStreamIdByResourceName(dataStream);
            if(!MyString.isEmptyOrNull(tmp)){
                dataStreamId = tmp;
            }
        }
        var result = getResourceByHour(deviceId, dataStreamId, MyDateTime.toLocalDateTime(beginTimestamp), MyDateTime.toLocalDateTime(endTimestamp));
        if(result == null) return PrettyHistoryDataResult.fail("获取数据为空");
        return PrettyHistoryDataResult.success(result);
    }

    @ApiOperation(value = "获取设备的某个资源历史数据，仅指定开始时间，结束时间为默认当前时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginTimestamp", value = "开始时间（时间戳）", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping(value = "data/history/pretty/{deviceId:^[1-9]\\d*$}/{dataStream}/{beginTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public PrettyHistoryDataResult getPrettyHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                                        @PathVariable(name = "dataStream") String dataStream,
                                                        @PathVariable(name = "beginTimestamp") Long beginTimestamp){
        String dataStreamId = null;
        if(OneNetKey.isDataStreamId(dataStream)){ // 为资源id
            dataStreamId = dataStream;
        }else { // 可能为资源名称
            var tmp = this.lampService.getDataStreamIdByResourceName(dataStream);
            if(!MyString.isEmptyOrNull(tmp)){
                dataStreamId = tmp;
            }
        }
        var result = getResourceByHour(deviceId, dataStreamId, MyDateTime.toLocalDateTime(beginTimestamp), LocalDateTime.now());
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
            @ApiImplicitParam(name = "regionName", value = "区域id或名字", required = true, dataType = "String", paramType = "path"),
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
            @ApiImplicitParam(name = "regionName", value = "区域Id或名字", required = true, dataType = "String", paramType = "path"),
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
            @ApiImplicitParam(name = "regionName", value = "区域Id或者名字", required = true, dataType = "string", paramType = "path"),
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
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
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
            @ApiImplicitParam(name = "regionName", value = "区域id或者名称", required = true, dataType = "string", paramType = "path"),
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
                        var tmp = (LastDataResult) this.getLastDataWithDeviceIdAndDataStream(deviceId, dataStream, request);
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
                    var tmp = (LastDataResult) this.getLastDataWithDeviceIdAndDataStream(deviceId, dataStream, request);
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
    @Override
    public LastDataResults getLastDataWithDeviceIdAndDataStreams(Integer deviceId, List<String> dataStreams, HttpServletRequest request) {
        try{
            if(!MyNumber.isPositive(deviceId) || dataStreams == null || dataStreams.isEmpty()){
                return LastDataResults.fail("错误的参数");
            }
            var map = new HashMap<String, LastData>();
            dataStreams.forEach(dataStream -> {
                if(!MyString.isEmptyOrNull(dataStream)){
                    var tmp = (LastDataResult) this.getLastDataWithDeviceIdAndDataStream(deviceId, dataStream, request);
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
    @Override
    public BaseApiResult getLastDataWithDeviceIdAndDataStream(Integer deviceId, String dataStream, HttpServletRequest request) {
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
                return ExecuteResult.success();
            }
            else {
                log.debug(ret.error);
                return ExecuteResult.fail("执行命令发生错误，" + ret.error);
            }

        }catch (Exception ex){
            log.error(ex.getMessage());
            return ExecuteResult.fail("执行命令发生错误," + ex.getMessage());
        }
    }

    private ExecuteResult execute(Integer deviceId, String imei, OneNetKey resource, short timeout, String cmd){
        try{
            var params = new CommandParams();
            params.deviceId = deviceId;
            params.imei = imei;
            params.oneNetKey = resource;
            params.timeout = timeout;
            params.command = cmd;
            var ret = oneNet.execute(params);
            if(ret.isOk()) {
                return ExecuteResult.success();
            }
            return ExecuteResult.fail("执行命令发生错误，" + ret.error);
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ExecuteResult.fail("执行命令发生错误，" + ex.getMessage());
        }
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
    public MiniStarResult newMiniStar(@ApiParam(value = "具体的指令集", required = true) @RequestBody List<OneNetExecuteArgs> data, HttpServletRequest request) {
        var result = new MiniStarResult();
        Map<Integer, BaseApiResult> map = null;
        var errCount = 0;
        try{
            map = this.miniStarService.miniStar(data);
            for(var r : map.entrySet()){
                if(!r.getValue().isOk()) errCount++;
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
            result.code = 1;
            result.msg = "发生错误";
            return result;
        }
        result.code = 0;
        result.msg = "下发成功，其中有"+errCount+"盏返回失败代码";
        result.values = map;
        return result;
    }

    @Override
    public MiniStarHistoryResult getMiniStarHistory(String userId, Integer count) {
        var result = new MiniStarHistoryResult();
        if(count != null && count > 0) {
            result.values = this.miniStarService.history(userId, count);
            result.code = 0;
            result.msg = "";
            return result;
        }
        result.code = 1;
        result.msg = "获取失败";
        return result;
    }

    @Override
    public MiniStarResult newDeviceMiniStar(Integer deviceId, Subtitle subtitle, HttpServletRequest request) {
        var result = new MiniStarResult();
        var map = new HashMap<Integer, BaseApiResult>();
        try{
            if(!subtitle.isValid()){
                result.code = 1;
                result.msg = "参数错误";
                return result;
            }
            var lamp = this.lampService.getLampByDeviceId(deviceId);
            var resource = this.lampService.getResourceByCommandValue(Command.ZNLD_DD_EXECUTE);
            if(lamp == null || resource == null){
                result.code = 1;
                result.msg = "参数错误";
                return result;
            }
            var ret = this.execute(deviceId, lamp.imei, resource.toOneNetKey(), resource.timeout, subtitle.toString());
            map.put(lamp.deviceId, new BaseApiResult(ret.code, ret.msg));
            if(ret.isOk()){
                result.code = 0;
                result.msg = "";
                result.values = map;
            }else{
                result.code = 1;
                result.msg = "下发失败";
                result.values = map;
            }
            return result;
        }catch (Exception ex){
            log.error(ex.getMessage());
            result.code = 1;
            result.msg = "发生错误";
            result.values = null;
            return result;
        }
    }

    @Override
    public PushResult pushByDeviceIdOfDataStream(Integer deviceId, String dataStream, Object value) {
        var ret = new PushResult();
        if(MyNumber.isNegativeOrZero(deviceId)){
            ret.code = 1;
            ret.msg = "参数错误";
            return ret;
        }
        var map = new HashMap<String ,BaseApiResult>();
        var dataStreamId = dataStream;
        if(!OneNetKey.isDataStreamId(dataStream)){ // 可能是资源名称
            dataStreamId = this.lampService.getDataStreamIdByResourceName(dataStream);
            if(MyString.isEmptyOrNull(dataStreamId)){
                ret.code = 1;
                ret.msg = "参数错误";
                return ret;
            }
        }
        var cmd = new CommandParams();
        cmd.deviceId = deviceId;
        cmd.oneNetKey = OneNetKey.from(dataStreamId);
        cmd.command = value.toString();
        var tmp = this.oneNet.execute(cmd);
        map.put(dataStream, new BaseApiResult(tmp.errno, tmp.error));
        ret.values = map;
        ret.code = 0;
        ret.msg = "";
        return ret;
    }

    @Override
    public PullResult pullByDeviceIdOfDataStream(Integer deviceId, String dataStream) {
        var ret = new PullResult();
        if(MyNumber.isNegativeOrZero(deviceId)){
            ret.code = 1;
            ret.msg = "参数错误";
            return ret;
        }
        var dataStreamId = dataStream;
        if(!OneNetKey.isDataStreamId(dataStream)){ // 可能是资源名称
            dataStreamId = this.lampService.getDataStreamIdByResourceName(dataStream);
            if(MyString.isEmptyOrNull(dataStream)){
                ret.code = 1;
                ret.msg = "参数错误";
                return ret;
            }
        }
        var lamp = this.lampService.getLampByDeviceId(deviceId);
        if(lamp == null){
            ret.code = 1;
            ret.msg = "参数错误";
            return ret;
        }
        var item = new PullResult.Item();
        item.deviceId = deviceId;
        item.deviceName = lamp.deviceName;

        var tmp = this.oneNet.getLastDataStreamById(deviceId, dataStreamId);
        if(!tmp.isOk()){
            var status = new PullResult.Item.ResourceStatus();
            status.name = dataStream;
            status.code = tmp.errno;
            status.msg = tmp.error;
            item.status.add(status);

            ret.code = 0;
            ret.msg = "";
            ret.values = item;
            return ret;
        }
        try{
            var status = new PullResult.Item.ResourceStatus();
            status.name = dataStream;
            status.code = tmp.errno;
            status.msg = tmp.error;
            status.value = tmp.data.currentValue;
            item.status.add(status);

            ret.values = item;
            ret.code = 0;
            ret.msg = "";
            return ret;
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        ret.code = 1;
        ret.msg = "发生错误";
        return ret;
    }

    @Override
    public PushResult pushByDeviceIdOfDataStreams(Integer deviceId, List<String> dataStreams, Object value) {
        var map = new HashMap<String ,BaseApiResult>();
        for(var ds : dataStreams){
            var ret = this.pushByDeviceIdOfDataStream(deviceId, ds, value);
            if(!ret.isOk()){
                map.put(ds, new BaseApiResult(ret.code, ret.msg));
            }else{
                map.put(ds, ret.values.get(ds));
            }
        }
        var result = new PushResult();
        result.code = 0;
        result.msg = "";
        result.values = map;
        return result;
    }

    @Override
    public PullResult pullByDeviceIdOfDataStreams(Integer deviceId, List<String> dataStreams) {
        var result = new PullResult();
        for(var ds : dataStreams){
            var ret = this.pullByDeviceIdOfDataStream(deviceId, ds);
            result.values.status.addAll(ret.values.status);
        }
        result.code = 0;
        result.msg = "";
        return result;
    }

    @Override
    public PushRegionResult pushByRegionOfDataStream(String region, String dataStream, Object value) {
        var result = new PushRegionResult();
        if(MyString.isEmptyOrNull(region)){
            result.code = 1;
            result.msg = "错误的参数";
            return result;
        }
        String regionId = null;
        if(MyString.isUuid(region)){ // 为区域id
            regionId = region;
        }else { // 可能是区域名字
            var regionModel = this.regionService.getRegionByName(region);
            if(regionModel != null) regionId = regionModel.id;
        }
        if(MyString.isEmptyOrNull(region)){
            result.code = 1;
            result.msg = "错误的参数";
            return result;
        }
        var lampMap = new HashMap<Integer ,Map<String, BaseApiResult>>();
        var lamps = this.lampService.getLampsByRegionId(regionId);
        for(var lamp : lamps){
            var ret = this.pushByDeviceIdOfDataStream(lamp.deviceId, dataStream, value);
            if(!ret.isOk()){
                var dsMap = new HashMap<String, BaseApiResult>();
                dsMap.put(dataStream, new BaseApiResult(ret.code, ret.msg));
                lampMap.put(lamp.deviceId, dsMap);
            }else{
                var dsMap = new HashMap<String, BaseApiResult>();
                var tmp = ret.values.get(dataStream);
                dsMap.put(dataStream, tmp);
                lampMap.put(lamp.deviceId, dsMap);
            }
        }
        result.code = 0;
        result.msg = "";
        result.values = lampMap;
        return result;
    }

    @Override
    public PullRegionResult pullByRegionOfDataStream(String region, String dataStream) {
        var result = new PullRegionResult();
        if(MyString.isEmptyOrNull(region)){
            result.code = 1;
            result.msg = "错误的参数";
            return result;
        }
        String regionId = null;
        if(MyString.isUuid(region)){ // 为区域id
            regionId = region;
        }else { // 可能是区域名字
            var regionModel = this.regionService.getRegionByName(region);
            if(regionModel != null) regionId = regionModel.id;
        }
        var values = new ArrayList<PullRegionResult.Item>();
        var lamps = this.lampService.getLampsByRegionId(regionId);
        for(var lamp : lamps){
            var ret = this.pullByDeviceIdOfDataStream(lamp.deviceId, dataStream);

            var item = new PullRegionResult.Item();
            item.deviceId = lamp.deviceId;
            item.deviceName = lamp.deviceName;

            var rs = new PullRegionResult.Item.ResourceStatus();
            var tmp = ret.values.status.get(0);
            rs.name = dataStream;
            rs.code = tmp.code;
            rs.msg = tmp.msg;
            rs.value = tmp.value;
            item.status.add(rs);
            values.add(item);
        }
        result.code = 0;
        result.msg = "";
        result.values = values;
        return result;
    }

    @Override
    public PullRegionResult pullByRegionOfDataStreamWithAngle(String region, String dataStream) {
        var ret = this.pullByRegionOfDataStream(region, dataStream);
        var angles = this.getRegionAngleStatus(region);

        angles.values.forEach(a -> {
            for(var it : ret.values){
                if(a.deviceId.equals(it.deviceId)){
                    var rs = new PullRegionResult.Item.ResourceStatus();
                    rs.name = "倾斜状态";
                    rs.value = a.value;
                    rs.code = a.code;
                    rs.msg = a.msg;
                    it.status.add(rs);
                }
            }
        });
        return ret;
    }

    @Override
    public PushRegionResult pushByRegionOfDataStreams(String region, List<String> dataStreams, Object value) {
        var result = new PushRegionResult();
        if(dataStreams == null || dataStreams.isEmpty()){
            result.code = 1;
            result.msg = "错误的参数";
            return result;
        }
        var lampMap = new HashMap<Integer ,Map<String, BaseApiResult>>();
        for(var ds : dataStreams){
            var ret = this.pushByRegionOfDataStream(region, ds, value);
            for(var l : ret.values.entrySet()){
                var key = l.getKey();
                var val = l.getValue();
                if(lampMap.containsKey(key)){
                    var tmp = lampMap.get(key);
                    val.forEach(tmp::put);
                }else{
                    lampMap.put(key, val);
                }
            }
        }
        result.code = 0;
        result.msg = "";
        result.values = lampMap;
        return result;
    }

    @Override
    public PullRegionResult pullByRegionOfDataStreams(String region, List<String> dataStreams) {
        var result = new PullRegionResult();
        if(MyString.isEmptyOrNull(region)){
            result.code = 1;
            result.msg = "错误的参数";
            return result;
        }
        if(dataStreams == null || dataStreams.isEmpty()){
            result.code = 1;
            result.msg = "错误的参数";
            return result;
        }
        List<PullRegionResult.Item> values = new ArrayList<>();
        for(var ds : dataStreams){
            var ret = this.pullByRegionOfDataStream(region, ds);
            if(values.stream().noneMatch(it -> ret.values.stream().anyMatch(it2 -> it2.deviceId.equals(it.deviceId)))){
                values.addAll(ret.values);
            }else{
                for(var item : values){
                    for(var it : ret.values){
                        if(it.deviceId.equals(item.deviceId)){
                            item.status.addAll(it.status);
                        }
                    }
                }
            }
        }
        result.code = 0;
        result.msg = "";
        result.values = values;
        return result;
    }

    @Override
    public AngleResult getAngleStatus(Integer deviceId) {
        var tmp = this.lampService.getLampAngleStatusByDeviceId(deviceId);
        var ret = new AngleResult();
        ret.code = 0;
        ret.msg = "";
        ret.deviceId = tmp.deviceId;
        ret.deviceName = tmp.deviceName;
        ret.value = tmp.value.toString();
        ret.code = tmp.code;
        ret.msg = tmp.msg;
        return ret;
    }

    @Override
    public AngleRegionResult getRegionAngleStatus(String region) {
        var result = new AngleRegionResult();
        if(MyString.isEmptyOrNull(region)){
            result.code = 1;
            result.msg = "错误的参数";
            return result;
        }
        String regionId = null;
        if(MyString.isUuid(region)){ // 为区域id
            regionId = region;
        }else { // 可能是区域名字
            var regionModel = this.regionService.getRegionByName(region);
            if(regionModel != null) regionId = regionModel.id;
        }
        var lamps = this.lampService.getLampsByRegionId(regionId);
        for(var lamp : lamps){
            var ret = this.getAngleStatus(lamp.deviceId);
            var item = new AngleRegionResult.Item();
            item.deviceId = ret.deviceId;
            item.deviceName = ret.deviceName;
            item.value = ret.value;
            item.code = ret.code;
            item.msg = ret.msg;
            result.values.add(item);
        }
        result.code = 0;
        result.msg = "";
        return result;
    }

    public void getWeightedData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp){
        var begin = MyDateTime.toLocalDateTime(beginTimestamp);
        var end = MyDateTime.toLocalDateTime(endTimestamp);
        var ret = this.oneNet.getWeightedData(deviceId, dataStreamId, begin, end);
    }
}
