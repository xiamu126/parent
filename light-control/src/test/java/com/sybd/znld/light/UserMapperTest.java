package com.sybd.znld.light;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.mapper.lamp.LampMapper;
import com.sybd.znld.mapper.lamp.LampStatisticsMapper;
import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.model.environment.RealTimeData;
import com.sybd.znld.model.lamp.LampStatisticsModel;
import com.sybd.znld.model.lamp.dto.LampStatistics;
import com.sybd.znld.model.onenet.Config;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.util.MyDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserMapperTest {
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
        var ret = this.lampStatisticsMapper.selectThisYearGroupDayByOrganId("88cc4ad365d9493f85db160b336c8414");
        log.debug(String.valueOf(ret.size()));
    }
}
