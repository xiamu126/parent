package com.sybd.znld.light;

import com.sybd.znld.model.lamp.ElectricityDispositionBoxModel;
import com.sybd.znld.model.lamp.LampModel;
import com.sybd.znld.model.lamp.LampModuleModel;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.onenet.IOneNetService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LampServiceTest {
    @Autowired
    private ILampService lampService;

    // 新增路灯
    @Test
    public void test(){
        var lamp = new LampModel();
        lamp.deviceName = "小岗村路灯01";
        lamp.deviceId = 10001;
        lamp.apiKey = "api key 1";
        lamp.imei = "imei 1";
        var regionId = "d4db3d36cbb843ca863b46153954b8d0";
        var modules = List.of("电子屏", "环境监测", "监控", "音柱");
        var ret = this.lampService.addLampToRegionWithModules(lamp, regionId, modules);
        Assert.assertNotNull(ret);
        Assert.assertEquals(ret.regionId, regionId);
        Assert.assertEquals(lamp.id, ret.lampId);
    }

    // 新增配电箱
    @Test
    public void test1(){
        var model = new ElectricityDispositionBoxModel();
        model.name = "小岗村配电箱01";
        model.deviceId = 20001;
        model.apiKey = "api key 2";
        model.imei = "imei 2";
        var regionId = "d4db3d36cbb843ca863b46153954b8d0";
        var ret = this.lampService.addElectricityBoxToRegion(model, regionId);
        Assert.assertNotNull(ret);
    }

    @Autowired
    private IOneNetService oneNetService;

    @Test
    public void test2() {
        var ret = this.oneNetService.isDeviceOnline("868194030013173");
        log.debug(String.valueOf(ret));
    }
}
