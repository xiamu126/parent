package com.sybd.znld.onenet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.model.lamp.dto.LampStatistic;
import com.sybd.znld.model.onenet.DataPushModel;
import com.sybd.znld.onenet.service.IMessageService;
import com.sybd.znld.util.MyDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MapperTest {
    @Autowired
    private DataDeviceOnOffMapper dataDeviceOnOffMapper;
    @Autowired
    private DataEnvironmentMapper dataEnvironmentMapper;
    @Autowired
    private DataAngleMapper dataAngleMapper;
    @Autowired
    private DataLocationMapper dataLocationMapper;

    @Autowired
    private RedissonClient redissonClient;


    @Test
    public void test4(){
        var ret = this.dataDeviceOnOffMapper.selectByDeviceIdAndResourceName(528130535,"一键报警开关");
        log.debug(ret.toString());
    }


    @Test
    public void test8(){
        var model = new DataPushModel();
        model.deviceId = 528792157;
        model.datastreamId = "3300_4_5700";
        model.imei = "868194030008504";
        model.name = "X-angle";
        model.value = "-0.3";
        model.at = LocalDateTime.now();
        var ret = this.dataAngleMapper.insert(model);
        Assert.assertTrue(ret > 0);
        model = this.dataAngleMapper.selectByDeviceIdAndResourceName(528792157, "X-angle");
        Assert.assertNotNull(model);
        model.value = 0;
        ret = this.dataAngleMapper.updateByDeviceIdAndResourceName(model);
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void test9(){
        var model = new DataPushModel();
        model.deviceId = 533263283;
        model.name = "北斗经度";
        model.value = 120.66124706341829;
        this.dataLocationMapper.updateLocationByDeviceIdAndResourceName(model);
        model.name = "北斗纬度";
        model.value = 31.150935273203878;
        this.dataLocationMapper.updateLocationByDeviceIdAndResourceName(model);
    }

    @Test
    public void test10(){
        var model = new DataPushModel();
        model.deviceId = 533263283;
        model.name = "北斗经度";
        model.locked = true;
        this.dataLocationMapper.updateLockStatusByDeviceIdAndResourceName(model);
        model.name = "北斗纬度";
        this.dataLocationMapper.updateLockStatusByDeviceIdAndResourceName(model);
    }

    @Test
    public void test11(){
        var map = this.redissonClient.getMap("com.sybd.znld.onenet.realtime." + 528130535);
        Assert.assertNotNull(map);
    }
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void test12() throws JsonProcessingException {
        var statistics = new LampStatistic();
        var msg = new LampStatistic.Message();
        msg.id = "23123";
        msg.voltage =  new LampStatistic.Message.ValueError<>(0.0, false);
        msg.brightness = new LampStatistic.Message.ValueError<>(0, false);
        msg.electricity = new LampStatistic.Message.ValueError<>(0.0, false);
        msg.energy = new LampStatistic.Message.ValueError<>(0.0, false);
        msg.power = new LampStatistic.Message.ValueError<>(0.0, false);
        msg.powerFactor = new LampStatistic.Message.ValueError<>(0.0, true);
        msg.rate = new LampStatistic.Message.ValueError<>(0.0, true);
        msg.updateTime = MyDateTime.toTimestamp(LocalDateTime.now());
        statistics.message = msg;
        var json = this.objectMapper.writeValueAsString(statistics);
        log.debug(json);
    }

    @Autowired
    private IMessageService messageService;
    @Test
    public void test13() {
        this.messageService.scheduledStatistics();
    }
}
