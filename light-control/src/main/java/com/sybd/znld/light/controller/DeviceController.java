package com.sybd.znld.light.controller;

import com.sybd.znld.mapper.lamp.OneNetResourceMapper;
import com.sybd.znld.model.environment.RealTimeData;
import com.sybd.znld.model.lamp.dto.LampStatistic;
import com.sybd.znld.model.lamp.dto.RegionBoxLamp;
import com.sybd.znld.light.service.IDeviceService;
import com.sybd.znld.model.lamp.dto.Message;
import com.sybd.znld.model.lamp.dto.OpResult;
import com.sybd.znld.model.onenet.Config;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.onenet.dto.BaseResult;
import com.sybd.znld.model.onenet.dto.CommandParams;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/device")
public class DeviceController implements IDeviceController {
    private final IDeviceService deviceService;
    private final IOneNetService oneNetService;
    private final ILampService lampService;
    private final RedissonClient redissonClient;
    private final OneNetResourceMapper oneNetResourceMapper;

    @Autowired
    public DeviceController(IDeviceService deviceService,
                            IOneNetService oneNetService,
                            ILampService lampService, RedissonClient redissonClient,
                            OneNetResourceMapper oneNetResourceMapper) {
        this.deviceService = deviceService;
        this.oneNetService = oneNetService;
        this.lampService = lampService;
        this.redissonClient = redissonClient;
        this.oneNetResourceMapper = oneNetResourceMapper;
    }

    @Override
    public OpResult operateDevice(String imei, String dataStream, Message.CommonAction action) {
        try {
            var cmd = new CommandParams();
            cmd.imei = imei;
            cmd.oneNetKey = OneNetKey.from(dataStream);
            cmd.command = String.valueOf(action.getValue());
            var result = this.oneNetService.executeAsync(cmd);
            var r = result.get();
            var opResult = new OpResult();
            opResult.values = new HashMap<>();
            opResult.values.put("dataStream", r);
            return opResult;
        }catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    @Override
    public OpResult offDevice(String imei, String dataStream) {
        if(MyString.isEmptyOrNull(imei) || MyString.isEmptyOrNull(dataStream)) {
            log.error("参数错误");
            return null;
        }
        var dataStreamId = dataStream;
        var dataStreamName = dataStream;
        if(!OneNetKey.isDataStreamId(dataStream)){ // 是资源名称
            dataStreamId = this.lampService.getDataStreamIdByResourceName(dataStream);
            if(MyString.isEmptyOrNull(dataStreamId)){
                log.error("错误的资源名称["+dataStream+"]");
                return null;
            }
        }
        if(OneNetKey.isDataStreamId(dataStream)){ // 是资源id
            var resource = this.oneNetResourceMapper.selectByOneNetKey(OneNetKey.from(dataStream));
            if(resource == null){
                log.error("错误的资源id["+dataStream+"]");
                return null;
            }
            dataStreamName = resource.description;

        }
        var cmd = new CommandParams();
        cmd.imei = imei;
        cmd.oneNetKey = OneNetKey.from(dataStreamId);
        cmd.command = "off";
        var ret = this.oneNetService.execute(cmd);
        if(ret.isOk()) {
            var map = this.redissonClient.getMap(Config.getRedisRealtimeKey(imei));
            var data = (RealTimeData) map.get(dataStreamName);
            data.value = false;
            map.put(dataStreamName, data);
        }
        var map = new HashMap<String, BaseResult>();
        map.put(dataStream, ret);
        var result = new OpResult();
        result.values = map;
        result.code = 0;
        result.msg = "";
        return result;
    }

    @Override
    public OpResult onDevice(String imei, String dataStream) {
        if(MyString.isEmptyOrNull(imei) || MyString.isEmptyOrNull(dataStream)) {
            log.error("参数错误");
            return null;
        }
        var dataStreamId = dataStream;
        var dataStreamName = dataStream;
        if(!OneNetKey.isDataStreamId(dataStream)){ // 可能是资源名称
            dataStreamId = this.lampService.getDataStreamIdByResourceName(dataStream);
            if(MyString.isEmptyOrNull(dataStreamId)){
                log.error("错误的资源名称["+dataStream+"]");
                return null;
            }
        }
        if(OneNetKey.isDataStreamId(dataStream)){ // 是资源id
            var resource = this.oneNetResourceMapper.selectByOneNetKey(OneNetKey.from(dataStream));
            if(resource == null){
                log.error("错误的资源id["+dataStream+"]");
                return null;
            }
            dataStreamName = resource.description;

        }
        var cmd = new CommandParams();
        cmd.imei = imei;
        cmd.oneNetKey = OneNetKey.from(dataStreamId);
        cmd.command = "on";
        var ret = this.oneNetService.execute(cmd);
        if(ret.isOk()) {
            var map = this.redissonClient.getMap(Config.getRedisRealtimeKey(imei));
            var data = (RealTimeData) map.get(dataStreamName);
            data.value = true;
            map.put(dataStreamName, data);
        }
        var map = new HashMap<String, BaseResult>();
        map.put(dataStream, ret);
        var result = new OpResult();
        result.values = map;
        result.code = 0;
        result.msg = "";
        return result;
    }

    @Override
    public Boolean isDeviceOn(String imei, String dataStream) {
        if(MyString.isEmptyOrNull(imei) || MyString.isEmptyOrNull(dataStream)) {
            log.error("参数错误");
            return null;
        }
        var map = this.redissonClient.getMap(Config.getRedisRealtimeKey(imei));
        if(map == null || map.isEmpty()) return null;
        RealTimeData result = null;
        var dataStreamId = dataStream;
        if(OneNetKey.isDataStreamId(dataStream)){ // 如果是资源id
            dataStreamId = this.lampService.getDataStreamIdByResourceName(dataStream);
            var resource = this.oneNetResourceMapper.selectByOneNetKey(OneNetKey.from(dataStreamId));
            if(resource == null){
                return null;
            }
            result = (RealTimeData) map.get(resource.description);

        } else {
            result = (RealTimeData) map.get(dataStream);
        }
        if(result == null) return null;
        return (Boolean) result.value;
    }

    @Override
    public List<RegionBoxLamp> getLamps(String organId) {
        try {
            return this.deviceService.getLamps(organId);
        }catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    @Override
    public LampStatistic getStatistics(String lampId){
        return this.deviceService.getStatistics(lampId);
    }
}
