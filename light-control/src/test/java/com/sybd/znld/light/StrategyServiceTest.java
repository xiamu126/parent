package com.sybd.znld.light;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.light.controller.dto.*;
import com.sybd.znld.light.service.IDeviceService;
import com.sybd.znld.light.service.IStrategyService;
import com.sybd.znld.model.lamp.LampAlarmModel;
import com.sybd.znld.model.lamp.Target;
import com.sybd.znld.model.lamp.dto.LampAlarm;
import com.sybd.znld.model.lamp.dto.LampAlarmOutput;
import com.sybd.znld.model.lamp.dto.Message;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.util.MyDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StrategyServiceTest {
    @Autowired
    private IStrategyService strategyService;
    @Autowired
    private IDeviceService deviceService;

    // 新增照明灯策略
    @Test
    public void test() {
    }

    // 新增配电箱策略
    @Test
    public void test2() {
    }

    // 获取某个组织下的所有照明灯策略的具体情况
    @Test
    public void test3() {
        var ret = this.strategyService.getLampStrategies("88cc4ad365d9493f85db160b336c8414");
        Assert.assertNotNull(ret);
        Assert.assertFalse(ret.isEmpty());
    }

    // 获取某个组织下的所有配电箱策略的具体情况
    @Test
    public void test4() {
    }

    // 测试下发照明灯指令
    @Test
    public void test5() {
    }

    @Test
    public void test8() {
        var strategies = this.strategyService.getLampStrategies("88cc4ad365d9493f85db160b336c8414");
        log.debug(strategies.toString());
    }

    @Test
    public void test9() {
        var result = this.deviceService.getLamps("88cc4ad365d9493f85db160b336c8414", "d4db3d36cbb843ca863b46153954b8d0");
        Assert.assertNotNull(result);
    }

    // 把小岗村下面的路灯添加到配电箱下面
    @Test
    public void test10() {
        var result = this.deviceService.addLampsOfRegionToBox("d4db3d36cbb843ca863b46153954b8d0", "0e88236629de4bf4b97225b95d3f8f9f");
        Assert.assertTrue(result);
    }

    @Test
    public void test11() {
        var ret = this.deviceService.addLampsToRegion(List.of("156effb2466e4c68b27d269726beb7e6",
                "5c4dc4b19213475498ddc6b5e14d3c3e",
                "123b93dd012142ee82deb3558d8df055"), "d4db3d36cbb843ca863b46153954b8d0");
        Assert.assertTrue(ret);
    }

    // 测试下发立即执行照明灯策略
    @Test
    public void test13() {
    }

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    public void test14() throws JsonProcessingException {
        var lampAlarm = new LampAlarm();
        var lampAlarmOutput = new LampAlarmOutput();
        lampAlarmOutput.id = "5d8040865f494830826c5c944bfda609";
        lampAlarmOutput.type = LampAlarmModel.AlarmType.COMMON.getDescribe();
        lampAlarmOutput.status = LampAlarmModel.Status.UNCONFIRMED.getDescribe();
        lampAlarmOutput.at = MyDateTime.toTimestamp(LocalDateTime.now());
        lampAlarmOutput.content = "失败重试达到上限";
        lampAlarmOutput.lampId = "156effb2466e4c68b27d269726beb7e6";
        lampAlarmOutput.lampName = "小岗村路灯01";
        lampAlarmOutput.regionName = "小岗村某某路";
        lampAlarm.message = lampAlarmOutput;
        var alarmMsg = this.objectMapper.writeValueAsString(lampAlarm);
        this.rabbitTemplate.convertAndSend(IOneNetService.ONENET_TOPIC_EXCHANGE, IOneNetService.ONENET_ALARM_MSG_LIGHT_ROUTING_KEY, alarmMsg);
    }

    @Test
    public void test15() {
        var statisticsMsg = "{\"at\":1577166899426,\"imei\":\"868194030013173\",\"type\":1,\"ds_id\":\"3300_0_5751\",\"value\":\"{'T':1577166943,'V':232.522,'HZ':49.985,'I':[0,3.319,0],'TE':4.549,'PP':[0,747.436,0],'PQ':[0,-9.626,0],'PS':[0,747.531,0],'EP':[0,0.067,0],'EQ':[0,0.001,0],'ES':[0,0.067,0],'TP':19,'HU':53,'RL':[1,0,1,0,0,0],'X':5.800,'Y':2.200,'B':100,'Ta':31.100,'Ua':59.500,'PM25':8.100,'PM10':16.500,'CO':0.389,'NO2':0.046,'SO2':0.073,'O3':0.079}\",\"dev_id\":575064769}\n";
        this.rabbitTemplate.convertAndSend(IOneNetService.ONENET_TOPIC_EXCHANGE, IOneNetService.ONENET_UP_MSG_LIGHT_ROUTING_KEY, statisticsMsg);
    }
}
