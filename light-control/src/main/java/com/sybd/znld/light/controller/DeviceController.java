package com.sybd.znld.light.controller;

import com.sybd.znld.light.controller.dto.Command;
import com.sybd.znld.light.controller.dto.RegionBoxLamp;
import com.sybd.znld.light.service.IDeviceService;
import com.sybd.znld.model.lamp.dto.OpResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/device")
public class DeviceController implements IDeviceController {
    private final IDeviceService deviceService;

    @Autowired
    public DeviceController(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public OpResult operateDevice(Integer deviceId, String dataStream, Command.Action action) {
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
