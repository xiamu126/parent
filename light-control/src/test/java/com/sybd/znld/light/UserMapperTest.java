package com.sybd.znld.light;

import com.sybd.znld.mapper.lamp.LampStatisticsMapper;
import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.model.lamp.LampStatisticsModel;
import com.sybd.znld.model.onenet.Config;
import com.sybd.znld.service.onenet.IOneNetService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
        var key = Config.getRedisRealtimeKey("868194030007522");
        var map = this.redissonClient.getMap(key);
        log.debug(map.getName());
        log.debug(String.valueOf(this.oneNetService.isDeviceOnline("868194030012126")));
    }

    @Autowired
    private LampStatisticsMapper lampStatisticsMapper;

    @Test
    public void test3() {
        var rand = new Random();
        for(var i = 0; i < 10; i++) {
            var model = new LampStatisticsModel();
            model.lampId = "156effb2466e4c68b27d269726beb7e6";
            model.regionId = "d4db3d36cbb843ca863b46153954b8d0";
            model.organId = "88cc4ad365d9493f85db160b336c8414";
            model.updateTime = LocalDateTime.now().minusHours(i);
            model.online = rand.nextBoolean();
            model.fault = rand.nextBoolean();
            model.light = rand.nextBoolean();
            model.electricity = rand.nextInt(100);
            this.lampStatisticsMapper.insert(model);
        }
    }
}
