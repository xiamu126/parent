package com.sybd.znld.service.znld;

import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.znld.LampResourceModel;
import com.sybd.znld.model.znld.OneNetResourceModel;
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
    public List<DeviceIdAndDeviceName> getDeviceIdAndDeviceNames() {
        return this.lampMapper.selectDeviceIdAndDeviceNames();
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
}
