package com.sybd.znld.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.controller.device.dto.*;
import com.sybd.znld.core.ApiResult;
import com.sybd.znld.core.ApiResultEx;
import com.sybd.znld.onenet.OneNetService;
import com.sybd.znld.onenet.dto.*;
import com.sybd.znld.service.ExecuteCommandService;
import com.sybd.znld.service.OneNetConfigDeviceService;
import com.sybd.znld.service.dto.CheckedResource;
import com.sybd.znld.service.dto.DeviceIdAndDeviceName;
import com.whatever.util.MyDateTime;
import com.whatever.util.MyString;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RestController
@RequestMapping("/api/v1/device")
public class DeviceController extends BaseDeviceController implements IDeviceController{

    private final OneNetConfigDeviceService oneNetConfigDeviceService;
    private final Logger log = LoggerFactory.getLogger(DeviceController.class);

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
        LocalDateTime end = MyDateTime.maxOfDay(begin);
        log.debug(begin.toString());
        log.debug(end.toString());
        return getResourceByHour(deviceId, oneNetKey, begin, end);
    }

    private Result getResourceByHour(Integer deviceId, OneNetKey oneNetKey, LocalDateTime begin, LocalDateTime end){
        try{
            //LocalDateTime now = LocalDateTime.now();
            LocalDateTime now = LocalDateTime.of(2018,9,17,23,59,59);
            LocalDateTime before = now.toLocalDate().atTime(0,0,1,0);
            if(begin.isEqual(end)){
                log.debug("开始时间等于结束时间");
                return null;
            }
            GetHistoryDataStreamResult result = this.oneNet.getHistoryDataStream(deviceId, oneNetKey.toDataStreamId(), before, now, 5000, null, null);//过去24小时内的数据
            List<GetHistoryDataStreamResult.DataPoint> data = (result.getData().getDataStreams().get(0)).getDataPoints();
            ArrayList<String> atList = new ArrayList<>();
            ArrayList<String> valueList = new ArrayList<>();
            HashSet<Integer> theSet = new HashSet<>();
            for(GetHistoryDataStreamResult.DataPoint theData : data){
                String str = theData.getAt();
                LocalDateTime theDate = LocalDateTime.parse(str, DateTimeFormatter.ofPattern(MyDateTime.format2));
                int hour = theDate.getHour();

                if (!theSet.contains(hour)) {
                    theSet.add(hour);
                    atList.add(hour +":00");
                    valueList.add(theData.getValue());
                }
            }
            Result ret = new Result();
            ret.atList = atList;
            ret.valueList = valueList;
            ret.title = MyString.replace("{}", end.toLocalDate().toString());
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
            GetDataStreamByIdResult ret = this.oneNet.getLastDataStreamById(deviceId, dataStreamId);
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
            GetLastDataStreamsResult ret = this.oneNet.getLastDataStream(deviceId);
            List<GetLastDataStreamsResult.DataStream> dataStreams = ret.getData().getDevices().get(0).getDataStreams();
            Map<String, String> map = this.oneNet.getInstanceMap(deviceId);
            HashMap<String, Object> resultMap = new HashMap<>();
            map.forEach((key, value) -> {
                GetLastDataStreamsResult.DataStream tmp = dataStreams.stream().filter(dataStream -> dataStream.getId().equals(value))
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
            GetLastDataStreamsResult data = oneNet.getLastDataStream(deviceId);
            OneNetKey oneNetKey = new OneNetKey(objId, objInstId, resId);
            oneNetKey.objId = objId;
            oneNetKey.objInstId = objInstId;
            oneNetKey.resId = resId;
            String dataStreamId = oneNet.getDataStreamId(oneNetKey);
            List<GetLastDataStreamsResult.DataStream> theData = (data.getData().getDevices().get(0)).getDataStreams();
            GetLastDataStreamsResult.DataStream jsonObj = theData.stream()
                    .filter(dataStream -> dataStream.getId().equals(dataStreamId))
                    .findFirst().orElse(null);
            final String weidu = this.oneNet.getOneNetKey("weidu");
            final String jingdu = this.oneNet.getOneNetKey("jingdu");
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

    /*@Deprecated
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
    }*/

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
            LocalDateTime begin = MyDateTime.toLocalDateTime(beginTimestamp);
            LocalDateTime end = MyDateTime.toLocalDateTime(endTimestamp);
            if(begin.isAfter(end)){
                return HistoryDataResult.fail("参数错误");
            }
            GetHistoryDataStreamResult ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, null, null, null);
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
            LocalDateTime begin = MyDateTime.toLocalDateTime(beginTimestamp);
            LocalDateTime end = MyDateTime.toLocalDateTime(endTimestamp);
            GetHistoryDataStreamResult ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, limit, null, null);
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
            LocalDateTime begin = MyDateTime.toLocalDateTime(beginTimestamp);
            LocalDateTime end = MyDateTime.toLocalDateTime(endTimestamp);
            GetHistoryDataStreamResult ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, limit, sort, null);
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
            LocalDateTime begin = MyDateTime.toLocalDateTime(beginTimestamp);
            LocalDateTime end = MyDateTime.toLocalDateTime(endTimestamp);
            if(begin.isAfter(end) || limit <= 0){
                return HistoryDataResult.fail("参数错误");
            }
            GetHistoryDataStreamResult ret = this.oneNet.getHistoryDataStream(deviceId, dataStreamId, begin, end, limit, sort, cursor);
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
            String cmd = command.getArgsEx();
            if(cmd.equals("")){
                return ExecuteResult.fail("非法的参数");
            }
            OneNetExecuteParams tmp = this.executeCommandService.getParamsByCommand(command.getArgs(), false);
            if(tmp == null) {
                return ExecuteResult.fail("获取命令失败");
            }
            String imei = oneNetConfigDeviceService.getImeiByDeviceId(deviceId);
            if(MyString.isEmptyOrNull(imei)){
                return ExecuteResult.fail("未找到指定设备的imei");
            }
            CommandParams params = new CommandParams(deviceId, imei, tmp.getOneNetKey(), tmp.getTimeout(), cmd);
            OneNetExecuteResult ret = oneNet.execute(params);
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
            List<DeviceIdAndDeviceName> tmp = this.oneNetConfigDeviceService.getDeviceIdAndDeviceNames();
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
            List<CheckedResource> ret = this.oneNetConfigDeviceService.getCheckedResources(deviceId);
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
        LocalDateTime begin = MyDateTime.toLocalDateTime(beginTimestamp);
        LocalDateTime end = MyDateTime.toLocalDateTime(endTimestamp);
        double ret = this.oneNet.getWeightedData(deviceId, dataStreamId, begin, end);
    }
}
