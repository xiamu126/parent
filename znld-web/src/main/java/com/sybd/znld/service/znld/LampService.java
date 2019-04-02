package com.sybd.znld.service.znld;

import com.sybd.znld.model.znld.LampResourceModel;
import com.sybd.znld.model.znld.OneNetResourceModel;
import com.sybd.znld.onenet.dto.OneNetKey;
import com.sybd.znld.service.znld.dto.CheckedResource;
import com.sybd.znld.service.znld.dto.DeviceIdAndDeviceName;
import com.sybd.znld.service.znld.mapper.LampMapper;
import com.sybd.znld.service.znld.mapper.LampResourceMapper;
import com.sybd.znld.service.znld.mapper.OneNetResourceMapper;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LampService implements ILampService {
    private final Logger log = LoggerFactory.getLogger(LampService.class);
    private final LampMapper lampMapper;
    private final LampResourceMapper lampResourceMapper;
    private final OneNetResourceMapper oneNetResourceMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public LampService(LampMapper lampMapper, LampResourceMapper lampResourceMapper, OneNetResourceMapper oneNetResourceMapper) {
        this.lampMapper = lampMapper;
        this.lampResourceMapper = lampResourceMapper;
        this.oneNetResourceMapper = oneNetResourceMapper;
    }

    @Override
    public boolean isDataStreamIdEnabled(Integer deviceId, OneNetKey key) {
        if(!MyNumber.isPositive(deviceId) || !key.isValid()) return false;
        var lamp = this.lampMapper.selectByDeviceId(deviceId);
        var resource = this.oneNetResourceMapper.selectByOneNetKey(key);
        if(lamp == null || resource == null) return false;
        var ret = this.lampResourceMapper.selectByLampIdAndResourceId(lamp.id, resource.id);
        return ret.status == LampResourceModel.Status.Monitor;
    }

    @Override
    public String getImeiByDeviceId(Integer deviceId) {
        if(!MyNumber.isPositive(deviceId)) return null;
        return this.lampMapper.selectImeiByDeviceId(deviceId);
    }

    @Override
    public List<DeviceIdAndDeviceName> getDeviceIdAndDeviceNames() {
        return this.lampMapper.selectDeviceIdAndDeviceNames();
    }

    @Override
    public List<CheckedResource> getCheckedResourceByDeviceId(Integer deviceId) {
        if(!MyNumber.isPositive(deviceId)) return null;
        var lamp = this.lampMapper.selectByDeviceId(deviceId);
        if(lamp == null) return null;
        var ret = this.lampResourceMapper.selectByLampId(lamp.id);
        var filteredRet = ret.stream().takeWhile(p -> p.status == LampResourceModel.Status.Monitor);
        var ids = filteredRet.map(t -> t.id).collect(Collectors.toList());
        var res = this.oneNetResourceMapper.selectByIds(ids);
        return res.stream().map(t -> {
            var key = OneNetKey.from(t.objId, t.objInstId, t.resId);
            var tmp = new CheckedResource();
            tmp.dataStreamId = key.toDataStreamId();
            tmp.description = t.description;
            return tmp;
        }).collect(Collectors.toList());
    }

    @Override
    public OneNetResourceModel getResourceByCommandValue(String cmd) {
        if(MyString.isEmptyOrNull(cmd)) return null;
        return this.oneNetResourceMapper.selectByCommandValue(cmd);
    }
}
