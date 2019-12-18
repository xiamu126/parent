package com.sybd.znld.light.controller;

import com.sybd.znld.light.controller.dto.Command;
import com.sybd.znld.light.controller.dto.RegionBoxLamp;
import com.sybd.znld.light.service.IDeviceService;
import com.sybd.znld.model.lamp.dto.Message;
import com.sybd.znld.model.lamp.dto.OpResult;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.onenet.dto.CommandParams;
import com.sybd.znld.service.onenet.IOneNetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

    @Autowired
    public DeviceController(IDeviceService deviceService, IOneNetService oneNetService) {
        this.deviceService = deviceService;
        this.oneNetService = oneNetService;
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
    public List<RegionBoxLamp> getLamps(String organId) {
        try {
            return this.deviceService.getLamps(organId);
        }catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }
}
