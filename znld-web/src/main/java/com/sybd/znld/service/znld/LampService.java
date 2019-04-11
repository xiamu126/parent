package com.sybd.znld.service.znld;

import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.znld.*;
import com.sybd.znld.service.znld.dto.CheckedResource;
import com.sybd.znld.service.znld.dto.DeviceIdAndDeviceName;
import com.sybd.znld.service.znld.mapper.*;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LampService implements ILampService {
    private final Logger log = LoggerFactory.getLogger(LampService.class);
    private final LampMapper lampMapper;
    private final LampResourceMapper lampResourceMapper;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final RegionMapper regionMapper;
    private final LampRegionMapper lampRegionMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public LampService(LampMapper lampMapper,
                       LampResourceMapper lampResourceMapper,
                       OneNetResourceMapper oneNetResourceMapper,
                       RegionMapper regionMapper,
                       LampRegionMapper lampRegionMapper) {
        this.lampMapper = lampMapper;
        this.lampResourceMapper = lampResourceMapper;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.regionMapper = regionMapper;
        this.lampRegionMapper = lampRegionMapper;
    }

    @Override
    public boolean isDataStreamIdEnabled(OneNetKey key) {
        if(!key.isValid()) return false;
        var resource = this.oneNetResourceMapper.selectByOneNetKey(key);
        if(resource == null) return false;
        return resource.status == OneNetResourceModel.Status.Monitor;
    }

    @Override
    public String getImeiByDeviceId(Integer deviceId) {
        if(!MyNumber.isPositive(deviceId)) return null;
        return this.lampMapper.selectImeiByDeviceId(deviceId);
    }

    @Override
    public List<DeviceIdAndDeviceName> getDeviceIdAndDeviceNames(String organId) {
        return this.lampMapper.selectDeviceIdAndDeviceNames(organId);
    }

    @Override
    public List<CheckedResource> getCheckedResourceByDeviceId(Integer deviceId) {
        if(!MyNumber.isPositive(deviceId)) return null;
        return this.lampMapper.selectCheckedResourceByDeviceId(deviceId);
    }

    @Override
    public OneNetResourceModel getResourceByCommandValue(String cmd) {
        if(MyString.isEmptyOrNull(cmd)) return null;
        return this.oneNetResourceMapper.selectByCommandValue(cmd);
    }

    @Override
    public LampRegionModel addLampToRegion(String lampId, String regionId) {
        if(!MyString.isUuid(lampId) || !MyString.isUuid(regionId)){
            log.debug("非法的参数");
            return null;
        }
        if(this.lampMapper.selectById(lampId) == null){
            log.debug("指定的路灯不存在");
            return null;
        }
        var thisRegion = this.regionMapper.selectById(regionId);
        if(thisRegion == null){
            log.debug("指定的区域不存在");
            return null;
        }
        // 一盏路灯只能属于一个物理区域，但可以属于多个虚拟区域
        var list = this.lampMapper.selectLampByLampIdAndRegionType(lampId, RegionModel.Type.PHYSICAL);
        if(list != null && !list.isEmpty() && thisRegion.type == RegionModel.Type.PHYSICAL){
            log.debug("此路灯已经属于某个物理区域，无法再将其添加到另一个物理区域");
            return null;
        }
        var model = new LampRegionModel();
        model.lampId = lampId;
        model.regionId = regionId;
        if(this.lampRegionMapper.insert(model) > 0)return model;
        return null;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public LampRegionModel addLampToRegion(LampModel lamp, String regionId, List<String> resourceIds) {
        if(lamp == null || !lamp.isValidBeforeInsert() || !MyString.isUuid(regionId) || resourceIds == null || resourceIds.isEmpty()){
            log.debug("非法的参数");
            return null;
        }
        // 检查deviceId，deviceName是否已经存在
        var tmp = this.lampMapper.selectByDeviceId(lamp.deviceId);
        if(tmp != null){
            log.debug("此设备id已经存在");
            return null;
        }
        tmp = this.lampMapper.selectByDeviceName(lamp.deviceName);
        if(tmp != null){
            log.debug("此设备名已经存在");
            return null;
        }
        // 经纬度如果非空，检查其唯一性
        if(lamp.isLongitudeLatitudeAssigned()){
            if(this.lampMapper.selectByLatitude(lamp.latitude) != null){
                log.debug("不能重复使用相同的纬度，此维度已包含于某一非报废路灯");
                return null;
            }
            if(this.lampMapper.selectByLongitude(lamp.longitude) != null){
                log.debug("不能重复使用相同的经度，此经度已包含于某一非报废路灯");
                return null;
            }
        }
        var region = this.regionMapper.selectById(regionId);
        if(region == null){
            log.debug("指定的区域不存在");
            return null;
        }
        // 开启事务
        if(this.lampMapper.insert(lamp) > 0){
            var lampRegionModel = new LampRegionModel();
            lampRegionModel.lampId = lamp.id;
            lampRegionModel.regionId = regionId;
            if(this.lampRegionMapper.insert(lampRegionModel) > 0) {
                // 关联路灯与资源
                resourceIds.forEach(resourceId -> {
                    var lampResource = new LampResourceModel();
                    lampResource.lampId = lamp.id;
                    lampResource.oneNetResourceId = resourceId;
                    if(this.lampResourceMapper.insert(lampResource) <= 0){
                        throw new RuntimeException("关联路灯与资源时发生失败，抛出异常，激活事务回滚");
                    }
                });
                // 关联资源成功
                return lampRegionModel;
            } else throw new RuntimeException("新增lampRegion失败，抛出异常，激活事务回滚");
        }
        return null;
    }
}
