package com.sybd.znld.mapper;

import com.sybd.znld.onenet.dto.OneNetKey;
import com.sybd.znld.service.dto.CheckedResource;
import com.sybd.znld.service.dto.DeviceIdAndDeviceName;
import com.sybd.znld.service.dto.DeviceIdAndImei;
import com.sybd.znld.service.dto.DeviceIdName;
import com.sybd.znld.dto.NameAndOneNetKey;
import com.sybd.znld.model.OneNetConfigDeviceEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OneNetConfigDeviceMapper {
    List<OneNetConfigDeviceEntity> getByDeviceId(Integer deviceId);
    String getDescBy(Integer objId, Integer objInstId, Integer resId);
    List<Integer> getDeviceIds();
    List<DeviceIdName> getDeviceIdNames();
    String getApiKeyByDeviceId(Integer deviceId);
    String getOneNetKey(String name);
    List<NameAndOneNetKey> getInstanceMap(Integer deviceId);
    String getImeiByDeviceId(Integer deviceId);
    List<DeviceIdAndImei> getDeviceIdAndImeis();
    List<DeviceIdAndDeviceName> getDeviceIdAndDeviceNames();
    List<CheckedResource> getCheckedResources(Integer deviceId);
    Boolean isDataStreamIdEnabled(OneNetKey oneNetKey);
}