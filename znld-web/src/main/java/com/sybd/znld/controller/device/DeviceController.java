package com.sybd.znld.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.controller.device.dto.*;
import com.sybd.onenet.model.OneNetKey;
import com.sybd.znld.onenet.IOneNetService;
import com.sybd.znld.onenet.OneNetService;
import com.sybd.znld.onenet.dto.*;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.service.znld.ILampService;
import com.sybd.znld.service.znld.IRegionService;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
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

@Slf4j
@Api(tags = "设备接口")
@RestController
@RequestMapping("/api/v1/device")
public class DeviceController implements IDeviceController{
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

    @GetMapping(value = "data/history/pretty/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public PrettyHistoryDataResult getPrettyHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                                        @PathVariable(name = "dataStreamId") String dataStreamId,
                                                        @PathVariable(name = "beginTimestamp") Long beginTimestamp){
        var result = getResourceByHour(deviceId, OneNetKey.from(dataStreamId), MyDateTime.toLocalDateTime(beginTimestamp), LocalDateTime.now());
        if(result == null) return PrettyHistoryDataResult.fail("获取数据为空");
        return PrettyHistoryDataResult.success(result);
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
            if(!this.lampService.isDataStreamIdEnabled(oneNetKey)){
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

    @ApiOperation(value = "获取某个设备的所有可用的资源Id和名字")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "resource/{deviceId:^[1-9]\\d*$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public CheckedResourcesResult getCheckedResources(@PathVariable("deviceId") Integer deviceId, HttpServletRequest request) {
        try{
            var ret = this.lampService.getCheckedResourceByDeviceId(deviceId);
            if(ret == null || ret.isEmpty()){
                return CheckedResourcesResult.fail("获取数据为空");
            }
            return CheckedResourcesResult.success(ret);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return CheckedResourcesResult.fail("获取数据失败");
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
