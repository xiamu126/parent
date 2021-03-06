package com.sybd.znld.light;

import com.sybd.znld.mapper.lamp.LampMapper;
import com.sybd.znld.model.lamp.LampModel;
import com.sybd.znld.service.onenet.IOneNetService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LampMapperTest {
    @Autowired
    private LampMapper lampMapper;

    @Test
    public void test(){
        var lamp = new LampModel();
        lamp.deviceName = "小岗村路灯03";
        lamp.deviceId = 10003;
        lamp.apiKey = "api key 3";
        lamp.imei = "imei 3";
        var ret = this.lampMapper.insert(lamp);
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void test2() {
        var model = this.lampMapper.selectByImei("868194030013173");
        log.debug(model.apiKey);
    }

    @Autowired
    private IOneNetService oneNetService;

    @Test
    public void test3() {
        var ret = this.oneNetService.isDeviceOnline("868194030013173");
        log.debug(String.valueOf(ret));
    }
}
