package com.sybd.znld.service.lamp;

import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.model.lamp.*;
import com.sybd.znld.model.lamp.dto.*;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LampService implements ILampService {
    private final LampMapper lampMapper;
    private final LampResourceMapper lampResourceMapper;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final RegionMapper regionMapper;
    private final LampRegionMapper lampRegionMapper;
    private final AppMapper appMapper;
    private final CameraMapper cameraMapper;
    private final LampCameraMapper lampCameraMapper;
    private final IOneNetService oneNetService;
    private final LampModuleMapper lampModuleMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public LampService(LampMapper lampMapper,
                       LampResourceMapper lampResourceMapper,
                       OneNetResourceMapper oneNetResourceMapper,
                       RegionMapper regionMapper,
                       LampRegionMapper lampRegionMapper,
                       AppMapper appMapper, CameraMapper cameraMapper, LampCameraMapper lampCameraMapper, IOneNetService oneNetService, LampModuleMapper lampModuleMapper) {
        this.lampMapper = lampMapper;
        this.lampResourceMapper = lampResourceMapper;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.regionMapper = regionMapper;
        this.lampRegionMapper = lampRegionMapper;
        this.appMapper = appMapper;
        this.cameraMapper = cameraMapper;
        this.lampCameraMapper = lampCameraMapper;
        this.oneNetService = oneNetService;
        this.lampModuleMapper = lampModuleMapper;
    }

    @Override
    public boolean isDataStreamIdEnabled(Integer deviceId, OneNetKey key) {
        if(!key.isValid()) return false;
        var resources = this.lampMapper.selectCheckedResourceByDeviceId(deviceId);
        if(resources == null || resources.isEmpty()) return false;
        return resources.stream().anyMatch(r -> r.dataStreamId.equals(key.toDataStreamId()));
    }

    @Override
    public String getImeiByDeviceId(Integer deviceId) {
        if(!MyNumber.isPositive(deviceId)) return null;
        return this.lampMapper.selectImeiByDeviceId(deviceId);
    }

    @Override
    public List<DeviceIdAndDeviceName> getDeviceIdAndDeviceNames(String organId) {
        var tmp = this.lampMapper.selectDeviceIdAndDeviceNames(organId);
        for(var d : tmp){

        }
        return tmp;
    }

    @Override
    public List<CheckedResource> getCheckedResourceByDeviceId(Integer deviceId) {
        if(!MyNumber.isPositive(deviceId)) return null;
        return this.lampMapper.selectCheckedResourceByDeviceId(deviceId);
    }

    @Override
    public List<CheckedResource> getCheckedEnvResourceByDeviceId(Integer deviceId) {
        if(!MyNumber.isPositive(deviceId)) return null;
        return this.lampMapper.selectCheckedEnvResourceByDeviceId(deviceId);
    }

    @Override
    public List<CheckedResource> getCheckedResourceByOrganId(String organId) {
        if(!MyString.isUuid(organId)) return null;
        var ret = this.lampMapper.selectByOrganId(organId,1,0);
        if(ret == null || ret.isEmpty()) return null;
        var lamp = ret.get(0);
        if(lamp == null) return null;
        return this.getCheckedResourceByDeviceId(lamp.deviceId);
    }

    @Override
    public List<CheckedResource> getCheckedEnvResourceByOrganId(String organId) {
        if(!MyString.isUuid(organId)) return null;
        var ret = this.lampMapper.selectByOrganId(organId,1,0);
        if(ret == null || ret.isEmpty()) return null;
        var lamp = ret.get(0);
        if(lamp == null) return null;
        return this.getCheckedEnvResourceByDeviceId(lamp.deviceId);
    }

    @Override
    public CheckedResource getCheckedEnvResourceByDeviceIdAndResourceDesc(Integer deviceId, String resourceDesc) {
        if(!MyNumber.isPositive(deviceId) || MyString.isEmptyOrNull(resourceDesc)) return null;
        return this.lampMapper.selectCheckedEnvResourceByDeviceIdAndResourceDesc(deviceId, resourceDesc);
    }

    @Override
    public CheckedResource getCheckedEnvResourceByOrganIdAndResourceDesc(String organId, String resourceDesc) {
        if(!MyString.isUuid(organId)) return null;
        var ret = this.lampMapper.selectByOrganId(organId,1,0);
        if(ret == null || ret.isEmpty()) return null;
        var lamp = ret.get(0);
        if(lamp == null) return null;
        return this.getCheckedEnvResourceByDeviceIdAndResourceDesc(lamp.deviceId, resourceDesc);
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
    public LampRegionModel addLampToRegionWithModules(LampModel lamp, String regionId, List<String> modules) {
        if(lamp == null){
            log.debug("lamp为空");
            return null;
        }
        if(!lamp.isValidForInsert()){
            log.debug("lamp实体类包含错误的数据,"+lamp.toString());
            return null;
        }
        var tmp = this.lampMapper.selectByDeviceId(lamp.deviceId);
        if(tmp != null){
            log.debug("这个设备id["+lamp.deviceId+"]已经绑定在其它路灯上");
            return null;
        }
        tmp = this.lampMapper.selectByDeviceName(lamp.deviceName);
        if(tmp != null){
            log.debug("这个设备名字["+lamp.deviceName+"]已经在使用");
            return null;
        }
        if(!MyString.isUuid(regionId)){
            log.debug("错误的区域id");
            return null;
        }
        var region = this.regionMapper.selectById(regionId);
        if(region == null){
            log.debug("指定的区域id不存在");
            return null;
        }
        if(modules.stream().anyMatch(MyString::isEmptyOrNull)){
            log.debug("模块包含空字符串");
            return null;
        }
        var moduleMap = new HashMap<String, LampModule>();
        for(var m : modules){
            var module = this.lampModuleMapper.selectByName(m);
            if(module == null){
                log.debug("指定的模块["+m+"]不存在");
                return null;
            }else{
                moduleMap.put(m, module);
            }
        }
        // 开启事务
        try{
            if(this.lampMapper.insert(lamp) > 0){
                var lampRegionModel = new LampRegionModel();
                lampRegionModel.lampId = lamp.id;
                lampRegionModel.regionId = regionId;
                if(this.lampRegionMapper.insert(lampRegionModel) > 0) {
                    // 关联路灯与功能模块
                    for(var m: modules){
                        var lampLampModule = new LampLampModule();
                        lampLampModule.lampId = lamp.id;
                        lampLampModule.lampModuleId = m;
                        if(this.lampResourceMapper.insert(lampResource) <= 0){
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return null;
                        }
                    }
                    // 关联资源成功
                    return lampRegionModel;
                } else {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return null;
                }
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, transactionManager = "znldTransactionManager")
    public LampRegionModel addLampToRegion(LampModel lamp, String regionId, List<String> resourceIds) {
        if(lamp == null || !lamp.isValidForInsert() || !MyString.isUuid(regionId) || resourceIds == null || resourceIds.isEmpty()){
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
                for(var resourceId: resourceIds){
                    var lampResource = new LampResourceModel();
                    lampResource.lampId = lamp.id;
                    lampResource.oneNetResourceId = resourceId;
                    if(this.lampResourceMapper.insert(lampResource) <= 0){
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return null;
                    }
                }
                // 关联资源成功
                return lampRegionModel;
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return null;
            }
        }
        return null;
    }

    @Override
    public List<LampModel> getLampsByRegionId(String regionId) {
        if(!MyString.isUuid(regionId)) return null;
        return this.lampMapper.selectByRegionId(regionId);
    }

    @Override
    public LampModel getLampByDeviceId(Integer deviceId) {
        return this.lampMapper.selectByDeviceId(deviceId);
    }

    @Override
    public LampAndCamera getActiveCameraByDeviceId(Integer deviceId) {
        if(!MyNumber.isPositive(deviceId)) return null;
        return this.lampMapper.selectActiveCameraByDeviceId(deviceId);
    }

    @Override
    public String getResourceNameByDataStreamId(String dataStreamId) {
        if(dataStreamId == null || !dataStreamId.matches("")) return null;
        var oneNetKey = OneNetKey.from(dataStreamId);
        var oneNetResource = this.oneNetResourceMapper.selectByOneNetKey(oneNetKey);
        if(oneNetResource != null) return oneNetResource.description;
        return null;
    }

    @Override
    public String getDataStreamIdByResourceName(String resourceName) {
        if(resourceName == null) return null;
        var oneNetResource = this.oneNetResourceMapper.selectByResourceName(resourceName);
        if(oneNetResource != null) return oneNetResource.objId+"_"+oneNetResource.objInstId+"_"+oneNetResource.resId;
        return null;
    }

    @Override
    public AppModel getAppInfoByName(String name) {
        if(MyString.isEmptyOrNull(name)) return null;
        return this.appMapper.selectByName(name);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, transactionManager = "znldTransactionManager")
    public LampCameraModel addCamera(String lampId, CameraModel model) {
        if(!MyString.isUuid(lampId) || model == null || !model.isValidForInsert()) {
            return null;
        }
        var count = this.cameraMapper.insert(model);
        if(count <= 0) {
            return null; // 添加失败
        }

        // 不再检查是否rtsp有重复，因为rtsp在不同的地方，但可以有相同的地址
       /* var camera = this.cameraMapper.selectByRtspUrl(model.rtspUrl);
        if(camera != null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null; // 已经添加过了
        }*/

       if(MyString.isUuid(model.id)){ // 如果已经插入过，则检测其是否存在
           var tmp = this.cameraMapper.selectById(model.id);
           if(tmp == null){
               TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
               return null; // 传入的参数有问题
           }
       }else{ // 否则先插入
           var tmp = this.cameraMapper.insert(model);
           if(tmp <= 0){
               TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
               return null; // 插入失败
           }
       }

        var lamp = this.lampMapper.selectById(lampId);
        if(lamp == null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null; // 指定的路灯不存在
        }

        var lampCameraModel = new LampCameraModel();
        lampCameraModel.lampId = lampId;
        lampCameraModel.cameraId = model.id;

        count = this.lampCameraMapper.insert(lampCameraModel);
        if( count <= 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }

        return lampCameraModel;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, transactionManager = "znldTransactionManager")
    public boolean removeCamera(String lampId, String cameraId) {
        if(!MyString.isUuid(lampId) || !MyString.isUuid(cameraId)) return false;
        var count = this.cameraMapper.deleteById(cameraId);
        if(count <= 0) return false;
        count = this.lampCameraMapper.deleteByLampIdAndCameraId(lampId, cameraId);
        if(count <= 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public LampStatus getLampStatusByDeviceId(Integer deviceId) {
        if(MyNumber.isNegativeOrZero(deviceId)){
            return null;
        }
        var lamp = this.lampMapper.selectByDeviceId(deviceId);
        if(lamp == null){
            return null;
        }

        var tmp = this.oneNetResourceMapper.selectByResourceName("电子屏开关");
        Integer eScreenStatus = null;
        OneNetKey oneNetKey = null;
        Object ret = null;
        if(tmp != null){
            oneNetKey = OneNetKey.from(tmp.objId, tmp.objInstId, tmp.resId);
            ret = this.oneNetService.getValue(deviceId, oneNetKey);
            eScreenStatus = MyNumber.getInteger(ret);
        }

        tmp = this.oneNetResourceMapper.selectByResourceName("景观灯开关");
        Integer miniStarStatus = null;
        if(tmp != null){
            oneNetKey = OneNetKey.from(tmp.objId, tmp.objInstId, tmp.resId);
            ret = this.oneNetService.getValue(deviceId, oneNetKey);
            miniStarStatus = MyNumber.getInteger(ret);
        }

        tmp = this.oneNetResourceMapper.selectByResourceName("互动屏开关");
        Integer iScreenStatus = null;
        if(tmp != null){
            oneNetKey = OneNetKey.from(tmp.objId, tmp.objInstId, tmp.resId);
            ret = this.oneNetService.getValue(deviceId, oneNetKey);
            iScreenStatus = MyNumber.getInteger(ret);
        }

        tmp = this.oneNetResourceMapper.selectByResourceName("风扇开关");
        Integer fanStatus = null;
        if(tmp != null){
            oneNetKey = OneNetKey.from(tmp.objId, tmp.objInstId, tmp.resId);
            ret = this.oneNetService.getValue(deviceId, oneNetKey);
            fanStatus = MyNumber.getInteger(ret);
        }

        tmp = this.oneNetResourceMapper.selectByResourceName("一键报警开关");
        Integer alarmStatus = null;
        if(tmp != null){
            oneNetKey = OneNetKey.from(tmp.objId, tmp.objInstId, tmp.resId);
            ret = this.oneNetService.getValue(deviceId, oneNetKey);
            alarmStatus = MyNumber.getInteger(ret);
        }

        tmp = this.oneNetResourceMapper.selectByResourceName("探针开关");
        Integer apStatus = null;
        if(tmp != null){
            oneNetKey = OneNetKey.from(tmp.objId, tmp.objInstId, tmp.resId);
            ret = this.oneNetService.getValue(deviceId, oneNetKey);
            apStatus = MyNumber.getInteger(ret);
        }

        tmp = this.oneNetResourceMapper.selectByResourceName("X-angle");
        Float xAngle = null;
        if(tmp != null){
            oneNetKey = OneNetKey.from(tmp.objId, tmp.objInstId, tmp.resId);
            var x = this.oneNetService.getLastDataStreamById(deviceId, oneNetKey.toDataStreamId());
            if(x != null && x.isOk()){
                xAngle = Float.parseFloat(x.data.currentValue.toString());
            }
        }

        tmp = this.oneNetResourceMapper.selectByResourceName("Y-angle");
        Float yAngle = null;
        if(tmp != null){
            oneNetKey = OneNetKey.from(tmp.objId, tmp.objInstId, tmp.resId);
            var y = this.oneNetService.getLastDataStreamById(deviceId, oneNetKey.toDataStreamId());
            if(y != null && y.isOk()){
                yAngle = Float.parseFloat(y.data.currentValue.toString());
            }
        }

        String xyStatus = null;
        if(xAngle == null || yAngle == null){
            xyStatus = "未知";
        }else {
            var xVal = Math.abs(xAngle - lamp.xAngle);
            var yVal = Math.abs(yAngle - lamp.yAngle);
            if(xVal <= 20 && yVal <= 20){
                xyStatus = "良好";
            }else if((xVal > 20 && xVal <= 100) || (yVal > 20 && yVal <= 100)) {
                xyStatus = "警告";
            }else{
                xyStatus = "危险";
            }
        }

        var region = this.lampMapper.selectRegionByLampId(lamp.id);
        if(region == null){
            return null;
        }

        var lampStatus = new LampStatus();
        lampStatus.lampId = lamp.id;
        lampStatus.deviceName = lamp.deviceName;
        lampStatus.regionName = region.name;
        lampStatus.miniStarStatus = miniStarStatus;
        lampStatus.iScreenStatus = iScreenStatus;
        lampStatus.eScreenStatus = eScreenStatus;
        lampStatus.alarmStatus = alarmStatus;
        lampStatus.apStatus = apStatus;
        lampStatus.fanStatus = fanStatus;
        lampStatus.xyAngle = xyStatus;

        return lampStatus;
    }

    @Override
    public DeviceStatus getLampAngleStatusByDeviceId(Integer deviceId) {
        if(MyNumber.isNegativeOrZero(deviceId)){
            return null;
        }
        var lamp = this.lampMapper.selectByDeviceId(deviceId);
        if(lamp == null){
            return null;
        }

        var deviceStatus = new DeviceStatus();
        var tmp = this.oneNetResourceMapper.selectByResourceName("X-angle");
        OneNetKey oneNetKey = null;
        Object ret = null;
        Float xAngle = null;
        if(tmp != null){
            oneNetKey = OneNetKey.from(tmp.objId, tmp.objInstId, tmp.resId);
            var x = this.oneNetService.getLastDataStreamById(deviceId, oneNetKey.toDataStreamId());
            if(x != null){
                deviceStatus.code = x.errno;
                deviceStatus.msg = x.error;
                if(x.isOk()) xAngle = Float.parseFloat(x.data.currentValue.toString());
            }
        }

        tmp = this.oneNetResourceMapper.selectByResourceName("Y-angle");
        Float yAngle = null;
        if(tmp != null){
            oneNetKey = OneNetKey.from(tmp.objId, tmp.objInstId, tmp.resId);
            var y = this.oneNetService.getLastDataStreamById(deviceId, oneNetKey.toDataStreamId());
            if(y != null){
                deviceStatus.code = y.errno;
                deviceStatus.msg = y.error;
                if(y.isOk()) yAngle = Float.parseFloat(y.data.currentValue.toString());
            }
        }

        String xyStatus = null;
        if(xAngle == null || yAngle == null){
            xyStatus = "未知";
        }else {
            var xVal = Math.abs(xAngle - lamp.xAngle);
            var yVal = Math.abs(yAngle - lamp.yAngle);
            if(xVal <= 20 && yVal <= 20){
                xyStatus = "良好";
            }else if((xVal > 20 && xVal <= 100) || (yVal > 20 && yVal <= 100)) {
                xyStatus = "警告";
            }else{
                xyStatus = "危险";
            }
        }

        var region = this.lampMapper.selectRegionByLampId(lamp.id);
        if(region == null){
            return null;
        }

        deviceStatus.deviceId = deviceId;
        deviceStatus.deviceName = lamp.deviceName;
        deviceStatus.value = xyStatus;

        return deviceStatus;
    }

    @Override
    public Map<Integer, LampStatus> getLampStatusByDeviceIds(List<Integer> deviceIds) {
        var ret = new HashMap<Integer, LampStatus>();
        deviceIds.forEach(deviceId -> {
            var tmp = this.getLampStatusByDeviceId(deviceId);
            ret.put(deviceId, tmp);
        });
        return ret;
    }

    @Override
    public Map<Integer, LampStatus> getLampStatusByRegionId(String regionId) {
        if(!MyString.isUuid(regionId)){
            return null;
        }
        var lamps = this.lampMapper.selectByRegionId(regionId);
        if(lamps == null || lamps.size() <= 0){
            return null;
        }
        var ids = lamps.stream().map(l -> l.deviceId).collect(Collectors.toList());
        return this.getLampStatusByDeviceIds(ids);
    }

    @Override
    public LampStatusResultPaged getLampStatusByRegionIdPaged(String regionId, int pageIndex, int pageSize) {
        if(!MyString.isUuid(regionId)){
            return null;
        }
        var count = this.lampMapper.selectCountOfLampByRegionId(regionId);
        if(count == null || count <= 0){
            return null;
        }
        var lamps = this.lampMapper.selectByRegionIdPaged(regionId, pageIndex, pageSize);
        if(lamps == null || lamps.size() <= 0){
            return null;
        }
        var ids = lamps.stream().map(l -> l.deviceId).collect(Collectors.toList());
        var status = this.getLampStatusByDeviceIds(ids);

        var ret = new LampStatusResultPaged();
        ret.status = status;
        ret.hasMore = (pageIndex * pageSize + pageSize) < count;
        return ret;
    }

    @Override
    public Integer getCountOfLampByRegionId(String regionId) {
        if(!MyString.isUuid(regionId)){
            return null;
        }
        return this.lampMapper.selectCountOfLampByRegionId(regionId);
    }
}
