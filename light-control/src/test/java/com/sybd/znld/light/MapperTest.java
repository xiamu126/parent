package com.sybd.znld.light;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.light.service.IReportService;
import com.sybd.znld.light.service.dto.Report;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.model.lamp.*;
import com.sybd.znld.model.lamp.dto.LampStatistics;
import com.sybd.znld.model.onenet.Config;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.util.MyDateTime;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.*;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IOneNetService oneNetService;

    @Test
    public void test(){
        var user = this.userMapper.selectById("a6b354d551f111e9804a0242ac110007");
        Assert.assertNotNull(user);
    }

    @Test
    public void test1(){
        var organ = this.organizationMapper.selectById("88cc4ad365d9493f85db160b336c8414");
        log.debug(organ.toString());
    }

    @Test
    public void test2() {
        var key = Config.getRedisRealtimeKey("868194030013223");
        var map = this.redissonClient.getMap(key);
        var electricity = map.get("electricity");
        var lastUpdateStatisticsTime = (Long) map.get("lastUpdateStatisticsTime"); // 上次更新数据库的时间
        var lastTime = MyDateTime.toLocalDateTime(lastUpdateStatisticsTime);
        var now = LocalDateTime.now();
        var hours = Duration.between(lastTime, now).abs().toHours();
        log.debug(String.valueOf(hours));
        log.debug(String.valueOf(electricity));
    }

    @Autowired
    private LampStatisticsMapper lampStatisticsMapper;

    @Test
    public void test3() {
        var rand = new Random();
        for(var i = 0; i < 10; i++) {
            var model = new LampStatisticsModel();
            model.lampId = "123b93dd012142ee82deb3558d8df055";
            model.regionId = "d4db3d36cbb843ca863b46153954b8d0";
            model.organId = "88cc4ad365d9493f85db160b336c8414";
            model.updateTime = LocalDateTime.now().minusDays(2);
            model.online = rand.nextBoolean();
            model.fault = rand.nextBoolean();
            model.light = rand.nextBoolean();
            model.electricity = rand.nextDouble();
            this.lampStatisticsMapper.insert(model);
        }
    }

    @Autowired
    private LampMapper lampMapper;

    @Test
    public void test4(){
        var ret = this.lampMapper.selectLampRegionOrganIdByImei("868194030013223");
        log.debug(ret.toString());
    }
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void test5() throws JsonProcessingException {
        var msg = "{'T':1576042498,'V':0.026,'HZ':49.970,'TP':34.520,'HU':0.120,'X':0.250,'Y':0.150,'B':80,'I':[0.003,0.003,0.003],'PP':[-0.003,-0.003,-0.003],'PQ':[-0.003,-0.003,-0.003],'PS':[0,0,0],'EP':[1.296,1.296,1.296],'EQ':[0.633,0.633,0.633],'ES':[1.643,1.643,1.643],'JDQ':[0,0,0,0,0,0]}";
        msg = msg.replaceAll("'","\"");
        var obj = this.objectMapper.readValue(msg, LampStatistics.class);
        log.debug(obj.toString());
    }

    @Test
    public void test6() {
        var ret = this.lampStatisticsMapper.selectThisWeekGroupDayByOrganId("88cc4ad365d9493f85db160b336c8414");
        var ret2 = this.lampStatisticsMapper.selectThisMonthGroupDayByOrganId("88cc4ad365d9493f85db160b336c8414");
        var ret3 = this.lampStatisticsMapper.selectThisYearGroupMonthByOrganId("88cc4ad365d9493f85db160b336c8414");
        log.debug(String.valueOf(ret.size()));
    }

    @Autowired
    private IReportService reportService;

    @Test
    public void test7() {
        var ret = this.reportService.getReport("88cc4ad365d9493f85db160b336c8414", Report.TimeType.MONTH);
        var ret1 = this.reportService.getReport("88cc4ad365d9493f85db160b336c8414", Report.TimeType.WEEK);
        log.debug(ret.toString());
    }

    @Autowired
    private LampStrategyMapper lampStrategyMapper;

    @Test
    public void test8() {
        var model = new LampStrategyModel();
        model.name = "照明灯策略1";
        model.fromDate = LocalDate.of(2020, 1,1);
        model.toDate = LocalDate.of(2020, 5,1);
        model.fromTime = LocalTime.of(17,0,1);
        model.toTime = LocalTime.of(6,0,1);
        model.autoGenerateTime = false;
        model.organId = "88cc4ad365d9493f85db160b336c8414";
        model.userId = "f1182e182aac4beb818559b5f47c176a";
        model.at1 = LocalTime.of(2,0);
        model.brightness1 = 70;
        model.at2 = LocalTime.of(4,0);
        model.brightness2 = 80;
        var ret = this.lampStrategyMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void test9() {
        var model = new LampStrategyModel();
        model.id = "1a1a9df0a5454753bf2e54ba1537618e";
        model.at1 = LocalTime.of(2,30);
        model.brightness1 = 40;
        model.status = LampStrategyModel.Status.OK;
        var ret = this.lampStrategyMapper.update(model);
        Assert.assertTrue(ret > 0);
    }

    @Autowired
    private LampExecutionMapper lampExecutionMapper;

    @Test
    public void test10(){
        var model = new LampExecutionModel();
        model.lampId = "156effb2466e4c68b27d269726beb7e6";
        model.lampStrategyId = "1a1a9df0a5454753bf2e54ba1537618e";
        model.mode = LampExecutionModel.Mode.STRATEGY;
        model.status = LampExecutionModel.Status.SUCCESS;
        var ret = this.lampExecutionMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    @Autowired
    private LampStrategyWaitingMapper lampStrategyWaitingMapper;

    @Test
    public void test11() {
        var model = new LampStrategyWaitingModel();
        model.lampId = "156effb2466e4c68b27d269726beb7e6";
        model.lampStrategyId = "1a1a9df0a5454753bf2e54ba1537618e";
        log.debug(YearMonth.of(1973,1).atEndOfMonth().toString());
        log.debug(YearMonth.of(1973,2).atEndOfMonth().toString());
        log.debug(YearMonth.of(1973,3).atEndOfMonth().toString());
        model.organId = "88cc4ad365d9493f85db160b336c8414";
        var ret = this.lampStrategyWaitingMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    @Autowired
    private LampAlarmMapper lampAlarmMapper;

    @Test
    public void test12() {
        var model = new LampAlarmModel();
        model.at = LocalDateTime.now();
        model.content = "测试报警内容";
        model.lampId = "156effb2466e4c68b27d269726beb7e6";
        model.lampName = "小岗村路灯01";
        model.organId = "88cc4ad365d9493f85db160b336c8414";
        model.regionId = "d4db3d36cbb843ca863b46153954b8d0";
        model.regionName = "小岗村某某路";
        model.status = LampAlarmModel.Status.UNCONFIRMED;
        model.type = LampAlarmModel.AlarmType.COMMON;
        var ret = this.lampAlarmMapper.insert(model);
        Assert.assertTrue(ret > 0);

        model = this.lampAlarmMapper.selectById("0ad28d8656b847c29136d63ecf536797");
        model.content = model.content+"123";
        ret = this.lampAlarmMapper.update(model);
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void test13() {
        var map = this.redissonClient.getMap(Config.getRedisRealtimeKey("868194030013173"));
        if(map != null) {
            log.debug("map.size()");
        }
    }

    @Test
    public void test14() {
        var days = Period.between(LocalDate.of(2019, 12,26), LocalDate.now()).getDays();
        log.debug(String.valueOf(days));
    }
}
