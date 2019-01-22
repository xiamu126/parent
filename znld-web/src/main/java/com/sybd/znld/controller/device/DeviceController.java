package com.sybd.znld.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.controller.device.dto.*;
import com.sybd.znld.core.ApiResult;
import com.sybd.znld.core.ApiResultEx;
import com.sybd.znld.onenet.*;
import com.sybd.znld.onenet.dto.*;
import com.sybd.znld.service.ExecuteCommandService;
import com.sybd.znld.service.OneNetConfigDeviceService;
import com.whatever.util.MyDateTime;
import com.whatever.util.MyString;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Api(tags = "设备接口")
@Slf4j
@RestController
@RequestMapping("/api/v1/device")
public class DeviceController extends BaseDeviceController implements IDeviceController{

    private final OneNetConfigDeviceService oneNetConfigDeviceService;

    @Autowired
    public DeviceController(RedisTemplate<String, Object> redisTemplate,
                            OneNetService oneNet,
                            ExecuteCommandService executeCommandService,
                            ProjectConfig projectConfig,
                            OneNetConfigDeviceService oneNetConfigDeviceService) {
        super(redisTemplate, oneNet, executeCommandService, projectConfig);
        this.oneNetConfigDeviceService = oneNetConfigDeviceService;
    }

    private Result getResourceByHour(Integer deviceId, OneNetKey oneNetKey, LocalDateTime begin) {
        var end = MyDateTime.maxOfDay(begin);
        log.debug(begin.toString());
        log.debug(end.toString());
        return getResourceByHour(deviceId, oneNetKey, begin, end);
    }

    private Result getResourceByHour(Integer deviceId, OneNetKey oneNetKey, LocalDateTime begin, LocalDateTime end){
        try{
            //LocalDateTime now = LocalDateTime.now();
            var now = LocalDateTime.of(2018,9,17,23,59,59);
            var before = now.toLocalDate().atTime(0,0,1,0);
            if(begin.isEqual(end)){
                log.debug("开始时间等于结束时间");
                return null;
            }
            var result = oneNet.getHistoryDataStream(deviceId, oneNetKey.toDataStreamId(), before, now, 5000, null, null);//过去24小时内的数据
            var data = (result.getData().getDatastreams()[0]).getDatapoints();
            var atList = new ArrayList<String>();
            var valueList = new ArrayList<String>();
            var theSet = new HashSet<Integer>();
            for(var theData : data){
                var str = theData.getAt();
                var theDate = LocalDateTime.parse(str, DateTimeFormatter.ofPattern(MyDateTime.format2));
                var hour = theDate.getHour();

                if (!theSet.contains(hour)) {
                    theSet.add(hour);
                    atList.add(hour +":00");
                    valueList.add(theData.getValue());
                }
            }
            var ret = new Result();
            ret.setAtList(atList);
            ret.setValueList(valueList);
            ret.setTitle(MyString.replace("{}", end.toLocalDate().toString()));
            return ret;
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
       return null;
    }

    @ApiOperation(value = "获取设备的最新数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "dataStreamId", value = "查看的数据流Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "data/last/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public LastDataResult getLastData(@PathVariable(name = "deviceId") Integer deviceId,
                                      @PathVariable(name = "dataStreamId") String dataStreamId) {
        try {
            if(!this.oneNetConfigDeviceService.isDataStreamIdEnabled(dataStreamId)){
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

    @Deprecated
    public ApiResult getLastData(Integer deviceId){
        try{
            var ret = this.oneNet.getLastDataStream(deviceId);
            var dataStreams = ret.getData().getDevices()[0].getDatastreams();
            var map = this.oneNet.getInstanceMap(deviceId);
            var resultMap = new HashMap<String, Object>();
            map.forEach((key, value) -> {
                var tmp = Arrays.stream(dataStreams).filter(dataStream -> dataStream.getId().equals(value))
                        .findFirst().orElse(null);
                resultMap.put(key, tmp);
            });
            return ApiResultEx.success(resultMap);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return ApiResult.fail("获取最新数据失败");
    }

    private double modifyLocation(String value){
        int idx = value.indexOf(".");
        double location = 0.0;
        if(idx != -1){
            double aft = Double.parseDouble(value.substring(idx-2));
            double pre = Double.parseDouble(value.substring(0, idx-2));
            location = pre+aft/60;
        }
        return location;
    }

    @Deprecated
    public ApiResult getLastData(Integer deviceId, Integer objId, Integer objInstId, Integer resId,
                                 HttpServletRequest request){
        try{
            var data = oneNet.getLastDataStream(deviceId);
            var oneNetKey = new OneNetKey(objId, objInstId, resId);
            oneNetKey.setObjId(objId);
            oneNetKey.setObjInstId(objInstId);
            oneNetKey.setResId(resId);
            var dataStreamId = oneNet.getDataStreamId(oneNetKey);
            var theData = (data.getData().getDevices()[0]).getDatastreams();
            var jsonObj = Arrays.stream(theData)
                    .filter(dataStream -> dataStream.getId().equals(dataStreamId))
                    .findFirst().orElse(null);
            final var weidu = this.oneNet.getOneNetKey("weidu");
            final var jingdu = this.oneNet.getOneNetKey("jingdu");
            if(dataStreamId.equals(weidu) || dataStreamId.equals(jingdu)){
                if(jsonObj != null){
                    jsonObj.setValue(modifyLocation(jsonObj.getValue().toString()));
                }
            }
            if(jsonObj == null){
                return ApiResult.fail("没有数据");
            }
            return ApiResult.success(jsonObj);
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ApiResult.fail("获取最新数据失败");
        }
    }
    @Deprecated
    public ApiResult getHistoryData(Integer deviceId, Integer objId, Integer objInstId, Integer resId, Long beginTimestamp, Long endTimestamp, Integer limit){
        try{
            var oneNetKey = new OneNetKey(objId, objInstId, resId);
            oneNetKey.setObjId(objId);
            oneNetKey.setObjInstId(objInstId);
            oneNetKey.setResId(resId);
            if(endTimestamp == 0){
                var ret = getResourceByHour(deviceId, oneNetKey, MyDateTime.toLocalDateTime(beginTimestamp));
                return ApiResult.success(ret);
            }else{
                var ret = getResourceByHour(deviceId, oneNetKey, MyDateTime.toLocalDateTime(beginTimestamp), MyDateTime.toLocalDateTime(endTimestamp));
                return ApiResult.success(ret);
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return ApiResult.fail("获取历史数据失败");
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
                                        @PathVariable(name = "endTimestamp") Long endTimestamp) {
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
                                    @PathVariable(name = "limit") Integer limit) {
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
                                    @PathVariable(name = "sort") String sort) {
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
    @PreAuthorize("isOk('ADMIN')")
    @GetMapping(value = "data/history/{deviceId:^[1-9]\\d*$}/{dataStreamId:^\\d+_\\d+_\\d+$}/{beginTimestamp:^\\d+$}/{endTimestamp:^\\d+$}/{limit:^[1-9]\\d*$}/{cursor:^\\d+_\\d+_\\d+$}/{sort:DESC|ASC}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public HistoryDataResult getHistoryData(@PathVariable(name = "deviceId") Integer deviceId,
                                    @PathVariable(name = "dataStreamId") String dataStreamId,
                                    @PathVariable(name = "beginTimestamp") Long beginTimestamp,
                                    @PathVariable(name = "endTimestamp") Long endTimestamp,
                                    @PathVariable(name = "limit") Integer limit,
                                    @PathVariable(name = "cursor") String cursor,
                                    @PathVariable(name = "sort") String sort) {
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
    public ExecuteResult execute(@PathVariable("deviceId") Integer deviceId, @ApiParam(value = "具体的命令", required = true) @RequestBody OneNetExecuteArgsEx command){
        try{
            var cmd = command.getArgsEx();
            if(cmd.equals("")){
                return ExecuteResult.fail("非法的参数");
            }
            var tmp = this.executeCommandService.getParamsByCommand(command.getArgs());
            if(tmp == null) {
                return ExecuteResult.fail("获取命令失败");
            }
            val imei = oneNetConfigDeviceService.getImeiByDeviceId(deviceId);
            if(MyString.isEmptyOrNull(imei)){
                return ExecuteResult.fail("未找到指定设备的imei");
            }
            var params = new CommandParams(deviceId, imei, tmp.getOneNetKey(), tmp.getTimeout(), cmd);
            var ret = oneNet.execute(params);
            if(ret.isOk()) return ExecuteResult.success(ret);

        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return ExecuteResult.fail("执行命令发生错误");
    }

    @ApiOperation(value = "获取所有的设备Id和名字")
    @GetMapping(value = "info", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public DeviceIdAndNameResult getDeviceIdAndName(){
        try{
            var tmp = this.oneNetConfigDeviceService.getDeviceIdAndDeviceNames();
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
    public CheckedResourcesResult getCheckedResources(@PathVariable("deviceId") Integer deviceId) {
        try{
            var ret = this.oneNetConfigDeviceService.getCheckedResources(deviceId);
            if(ret == null || ret.isEmpty()){
                return CheckedResourcesResult.fail("获取数据为空");
            }
            return CheckedResourcesResult.success(ret);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return CheckedResourcesResult.fail("获取数据失败");
    }

    public void getWeightedData(Integer deviceId, String dataStreamId, Long beginTimestamp, Long endTimestamp){
        var begin = MyDateTime.toLocalDateTime(beginTimestamp);
        var end = MyDateTime.toLocalDateTime(endTimestamp);
        var ret = this.oneNet.getWeightedData(deviceId, dataStreamId, begin, end);
    }
}
