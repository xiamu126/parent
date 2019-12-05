package com.sybd.znld.light.service;

import com.sybd.znld.light.controller.dto.RegionBoxLamp;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.model.DeviceStatus;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxLampModel;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxModel;
import com.sybd.znld.model.lamp.LampModel;
import com.sybd.znld.model.lamp.LampRegionModel;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeviceService implements IDeviceService {
    private final RegionMapper regionMapper;
    private final LampMapper lampMapper;
    private final RedissonClient redissonClient;
    private final ElectricityDispositionBoxMapper boxMapper;
    private final ElectricityDispositionBoxLampMapper boxLampMapper;
    private final LampModuleMapper lampModuleMapper;
    private final OrganizationMapper organizationMapper;
    private final LampRegionMapper lampRegionMapper;

    @Autowired
    public DeviceService(RegionMapper regionMapper,
                         LampMapper lampMapper,
                         RedissonClient redissonClient,
                         ElectricityDispositionBoxMapper boxMapper,
                         ElectricityDispositionBoxLampMapper boxLampMapper,
                         LampModuleMapper lampModuleMapper,
                         OrganizationMapper organizationMapper,
                         LampRegionMapper lampRegionMapper) {
        this.regionMapper = regionMapper;
        this.lampMapper = lampMapper;
        this.redissonClient = redissonClient;
        this.boxMapper = boxMapper;
        this.boxLampMapper = boxLampMapper;
        this.lampModuleMapper = lampModuleMapper;
        this.organizationMapper = organizationMapper;
        this.lampRegionMapper = lampRegionMapper;
    }

    @Override
    public RegionBoxLamp getLamps(String organId, String regionId) {
        if (!MyString.isUuid(organId)) {
            log.error("传入的组织id[" + organId + "]错误");
            return null;
        }
        if (!MyString.isUuid(regionId)) {
            log.error("传入的区域id[" + regionId + "]错误");
            return null;
        }
        var region = this.regionMapper.selectById(regionId);
        if (region == null) {
            log.error("传入的区域id[" + regionId + "]不存在");
            return null;
        }
        if (!region.organizationId.equals(organId)) {
            log.error("传入的区域id[" + regionId + "]与组织id[" + organId + "]不符");
            return null;
        }
        var result = new RegionBoxLamp();
        result.id = regionId;
        result.name = region.name;
        var boxes = this.regionMapper.selectBoxesByOrganIdRegionId(organId, regionId);
        if (boxes == null || boxes.isEmpty()) return null;
        for (var box : boxes) {
            if (box == null) continue;
            var map = this.redissonClient.getMap("com.sybd.znld.onenet.realtime." + box.deviceId);
            var tmpBox = new RegionBoxLamp.Box();
            tmpBox.imei = box.imei;
            tmpBox.name = box.name;
            if (map != null) {
                tmpBox.lng = MyNumber.getDouble(map.get("百度经度"));
                tmpBox.lat = MyNumber.getDouble(map.get("百度纬度"));
            }
            switch (box.status) {
                case OK:
                    tmpBox.status = "正常";
                    break;
                case ERROR:
                    tmpBox.status = "发生故障";
                    break;
                case DEAD:
                    tmpBox.status = "报废了";
                    break;
                default:
                    tmpBox.status = "未知";
            }
            var lamps = this.boxMapper.selectLampsByBoxId(box.id);
            if (lamps != null && !lamps.isEmpty()) {
                for (var lamp : lamps) {
                    if (lamp == null) continue;
                    map = this.redissonClient.getMap("com.sybd.znld.onenet.realtime." + lamp.deviceId);
                    var tmpLamp = new RegionBoxLamp.Box.Lamp();
                    tmpLamp.imei = lamp.imei;
                    tmpLamp.name = lamp.deviceName;
                    if (map != null) {
                        tmpLamp.lng = MyNumber.getDouble(map.get("百度经度"));
                        tmpLamp.lat = MyNumber.getDouble(map.get("百度纬度"));
                    }
                    switch (lamp.status) {
                        case OK:
                            tmpBox.status = "正常";
                            break;
                        case ERROR:
                            tmpBox.status = "发生故障";
                            break;
                        case DEAD:
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
    public List<RegionBoxLamp> getLamps(String organId) {
        if (!MyString.isUuid(organId)) {
            log.error("传入的组织id[" + organId + "]错误");
            return null;
        }
        var organ = this.organizationMapper.selectById(organId);
        if (organ == null) {
            log.error("传入的组织id[" + organId + "]不存在");
            return null;
        }
        var regions = this.regionMapper.selectByOrganId(organId);
        if (regions == null || regions.isEmpty()) {
            log.error("这个组织[" + organId + "]下面没有任何区域");
            return null;
        }
        var result = new ArrayList<RegionBoxLamp>();
        for (var region : regions) {
            var tmp = this.getLamps(organId, region.regionId);
            result.add(tmp);
        }
        return result;
    }

    @Transactional(transactionManager = "znldTransactionManager")
    @Override
    public boolean insertAndAddLampsToRegion(List<LampModel> lamps, String regionId) {
        try {
            if (lamps == null || lamps.isEmpty()) {
                return false;
            }
            if (!MyString.isUuid(regionId)) {
                return false;
            }
            var region = this.regionMapper.selectById(regionId);
            if (region == null) {
                return false;
            }
            for (var lamp : lamps) {
                if(!lamp.isValidForInsert()) return false;
                if(this.lampMapper.selectByDeviceName(lamp.deviceName) != null) {
                    log.error("重复的设备名称["+lamp.deviceName+"]");
                    return false;
                }
                if(this.lampMapper.selectByDeviceId(lamp.deviceId) != null) {
                    log.error("重复的设备id["+lamp.deviceId+"]");
                    return false;
                }
                if(this.lampMapper.selectByImei(lamp.imei) != null) {
                    log.error("重复的imei["+lamp.imei+"]");
                    return false;
                }
            }
            for (var lamp : lamps) {
                this.lampMapper.insert(lamp);
                if (this.lampMapper.selectById(lamp.id) == null) {
                    // 这个id不存在
                    log.error("这个路灯id["+lamp.id+"]不存在");
                    continue;
                }
                if (this.lampRegionMapper.selectByLampIdAndRegionId(lamp.id, regionId) != null) {
                    // 已经关联过了
                    log.error("这个路灯id["+lamp.id+"]和这个区域id["+regionId+"]已经关联过了");
                    continue;
                }
                var lampRegion = this.lampRegionMapper.selectByLampId(lamp.id);
                if(lampRegion != null) {
                    log.error("这个路灯id["+lamp.id+"]已经关联到了这个区域id["+lampRegion.regionId+"]");
                    continue;
                }
                var tmp = new LampRegionModel();
                tmp.lampId = lamp.id;
                tmp.regionId = regionId;
                this.lampRegionMapper.insert(tmp);
            }
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public boolean insertAndAddBoxesToRegion(List<ElectricityDispositionBoxModel> boxes, String regionId) {
        return false;
    }

    @Transactional(transactionManager = "znldTransactionManager")
    @Override
    public boolean addLampsToRegion(List<String> ids, String regionId) {
        try {
            if (ids == null || ids.isEmpty()) {
                return false;
            }
            if (!MyString.isUuid(regionId)) {
                return false;
            }
            var region = this.regionMapper.selectById(regionId);
            if (region == null) {
                return false;
            }
            for (var id : ids) {
                if (this.lampMapper.selectById(id) == null) {
                    // 这个id不存在
                    log.error("这个路灯id["+id+"]不存在");
                    continue;
                }
                if (this.lampRegionMapper.selectByLampIdAndRegionId(id, regionId) != null) {
                    log.error("这个路灯id["+id+"]和这个区域id["+regionId+"]已经关联过了");
                    // 已经关联过了
                    continue;
                }
                var lampRegion = this.lampRegionMapper.selectByLampId(id);
                if(lampRegion != null) {
                    log.error("这个路灯id["+id+"]已经关联到了这个区域id["+lampRegion.regionId+"]");
                    continue;
                }
                var tmp = new LampRegionModel();
                tmp.lampId = id;
                tmp.regionId = regionId;
                this.lampRegionMapper.insert(tmp);
            }
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public boolean addBoxesToRegion(List<String> ids, String regionId) {
        return false;
    }

    @Override
    public boolean addLampsOfRegionToBox(String regionId, String boxId) {
        if (!MyString.isUuid(regionId)) return false;
        if (!MyString.isUuid(boxId)) return false;
        if (this.regionMapper.selectById(regionId) == null) return false;
        if (this.boxMapper.selectById(boxId) == null) return false;

        var lamps = this.regionMapper.selectLampsByRegionId(regionId);
        var boxLamps = this.boxLampMapper.selectByBoxId(boxId);
        if (lamps != null && !lamps.isEmpty()) {
            for (var lamp : lamps) { // 准备开始循环的一个个关联
                if (boxLamps != null && !boxLamps.isEmpty()) { // 如果这个配电箱下面已经有路灯，那么看看这些路灯里面是否已经包含了当前要添加的这个路灯
                    var alreadyAssociated = boxLamps.stream().anyMatch(l -> l.lampId.equals(lamp.id));
                    if (!alreadyAssociated) { // 只有没有关联过才会添加
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
