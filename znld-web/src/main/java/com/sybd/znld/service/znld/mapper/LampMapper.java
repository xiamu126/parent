package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.znld.LampModel;
import com.sybd.znld.onenet.dto.OneNetKey;
import com.sybd.znld.service.znld.dto.CheckedResource;
import com.sybd.znld.service.znld.dto.DeviceIdAndDeviceName;
import com.sybd.znld.service.znld.dto.DeviceIdAndIMEI;
import com.sybd.znld.service.znld.dto.DeviceIdName;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
@DbSource("znld")
public interface LampMapper {
    List<LampModel> selectByDeviceId(Integer deviceId);
    LampModel selectByOneNetKey(OneNetKey oneNetKey);
    List<Integer> selectAllDeviceIds();
    Map<String, String> selectAllDeviceIdNames();
    String selectApiKeyByDeviceId(Integer deviceId);
    String selectOneNetKeyByResourceName(String name);
    HashMap<String, String> selectResourceMapByDeviceId(Integer deviceId);
    String selectImeiByDeviceId(Integer deviceId);
    Map<Integer, String> selectAllDeviceIdAndIMEI();
    List<DeviceIdAndDeviceName> selectDeviceIdAndDeviceNames();
    List<CheckedResource> selectCheckedResourcesByDeviceId(Integer deviceId);
    LampModel selectCheckedByOneNetKey(OneNetKey oneNetKey);
}
