package com.sybd.znld.light.service;

import com.sybd.znld.light.controller.dto.RegionBoxLamp;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxLampModel;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class DeviceService implements IDeviceService {
    private final RegionMapper regionMapper;
    private final LampMapper lampMapper;
    private final RedissonClient redissonClient;
    private final ElectricityDispositionBoxMapper boxMapper;
    private final ElectricityDispositionBoxLampMapper boxLampMapper;
    private final LampModuleMapper lampModuleMapper;

    @Autowired
    public DeviceService(RegionMapper regionMapper,
                         LampMapper lampMapper,
                         RedissonClient redissonClient,
                         ElectricityDispositionBoxMapper boxMapper,
                         ElectricityDispositionBoxLampMapper boxLampMapper,
                         LampModuleMapper lampModuleMapper) {
        this.regionMapper = regionMapper;
        this.lampMapper = lampMapper;
        this.redissonClient = redissonClient;
        this.boxMapper = boxMapper;
        this.boxLampMapper = boxLampMapper;
        this.lampModuleMapper = lampModuleMapper;
    }

    @Override
    public RegionBoxLamp getLamps(String organId, String regionId) {
        var region = this.regionMapper.selectById(regionId);
        if(region == null) return null;
        var result = new RegionBoxLamp();
        result.id = regionId;
        result.name = region.name;
        var boxes = this.regionMapper.selectBoxesByOrganIdRegionId(organId, regionId);
        if (boxes == null || boxes.isEmpty()) return null;
        for (var box : boxes) {
            if(box == null) continue;
            var map = this.redissonClient.getMap("com.sybd.znld.onenet.realtime." + box.deviceId);
            var tmpBox = new RegionBoxLamp.Box();
            tmpBox.imei = box.imei;
            tmpBox.name = box.name;
            if (map != null) {
                tmpBox.lng = MyNumber.getDouble(map.get("百度经度"));
                tmpBox.lat = MyNumber.getDouble(map.get("百度纬度"));
            }
            switch (box.status) {
                case LAMP_OK:
                    tmpBox.status = "正常";
                    break;
                case LAMP_ERROR:
                    tmpBox.status = "发生故障";
                    break;
                case LAMP_DEAD:
                    tmpBox.status = "报废了";
                    break;
                default:
                    tmpBox.status = "未知";
            }
            var lamps = this.boxMapper.selectLampsByBoxId(box.id);
            if (lamps != null && !lamps.isEmpty()) {
                for (var lamp : lamps) {
                    if(lamp == null) continue;
                    map = this.redissonClient.getMap("com.sybd.znld.onenet.realtime." + lamp.deviceId);
                    var tmpLamp = new RegionBoxLamp.Box.Lamp();
                    tmpLamp.imei = lamp.imei;
                    tmpLamp.name = lamp.deviceName;
                    if (map != null) {
                        tmpLamp.lng = MyNumber.getDouble(map.get("百度经度"));
                        tmpLamp.lat = MyNumber.getDouble(map.get("百度纬度"));
                    }
                    switch (lamp.status) {
                        case LAMP_OK:
                            tmpBox.status = "正常";
                            break;
                        case LAMP_ERROR:
                            tmpBox.status = "发生故障";
                            break;
                        case LAMP_DEAD:
                            tmpBox.status = "报废了";
                            break;
                        default:
                            tmpBox.status = "未知";
                    }
                    var modules = this.lampModuleMapper.selectModulesByLampId(lamp.id);
                    if (modules != null && !modules.isEmpty()) {
                        tmpLamp.modules = modules.stream().map(m -> m.name).collect(Collectors.toList());
                    }
                    tmpBox.lamps.add(tmpLamp);
                }
            }
            result.boxes.add(tmpBox);
        }
        return result;
    }

    @Override
    public boolean addLampsOfRegionToBox(String regionId, String boxId) {
        if(!MyString.isUuid(regionId)) return false;
        if(!MyString.isUuid(boxId)) return false;
        if(this.regionMapper.selectById(regionId) == null) return false;
        if(this.boxMapper.selectById(boxId) == null) return false;

        var lamps = this.regionMapper.selectLampsByRegionId(regionId);
        var boxLamps = this.boxLampMapper.selectByBoxId(boxId);
        if(lamps != null && !lamps.isEmpty()){
            for(var lamp : lamps) { // 准备开始循环的一个个关联
                if(boxLamps != null && !boxLamps.isEmpty()) { // 如果这个配电箱下面已经有路灯，那么看看这些路灯里面是否已经包含了当前要添加的这个路灯
                    var alreadyAssociated = boxLamps.stream().anyMatch(l -> l.lampId.equals(lamp.id));
                    if(!alreadyAssociated) { // 只有没有关联过才会添加
                        var model = new ElectricityDispositionBoxLampModel();
                        model.electricityDispositionBoxId = boxId;
                        model.lampId = lamp.id;
                        this.boxLampMapper.insert(model);
                    }
                } else { // 当前这个配电箱下面没有关联任何路灯
                    var model = new ElectricityDispositionBoxLampModel();
                    model.lampId = lamp.id;
                    model.electricityDispositionBoxId = boxId;
                    this.boxLampMapper.insert(model); // 直接添加
                }
            }
        }
        return true;
    }
}
