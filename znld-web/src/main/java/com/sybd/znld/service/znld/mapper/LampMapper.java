package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.znld.LampModel;
import com.sybd.znld.service.znld.dto.CheckedResource;
import com.sybd.znld.service.znld.dto.DeviceIdAndDeviceName;
import com.sybd.znld.service.znld.dto.DeviceIdAndIMEI;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
@DbSource("znld")
public interface LampMapper {
    LampModel selectByDeviceId(Integer deviceId);
    List<Integer> selectAllDeviceIds();
    String selectApiKeyByDeviceId(Integer deviceId);
    HashMap<String, String> selectResourceMapByDeviceId(Integer deviceId);
    String selectImeiByDeviceId(Integer deviceId);
    Map<Integer, String> selectAllDeviceIdAndIMEI();
    List<DeviceIdAndDeviceName> selectDeviceIdAndDeviceNames();
    List<CheckedResource> selectCheckedResourceByDeviceId(Integer deviceId);
}
