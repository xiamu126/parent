package com.sybd.znld.environment;

import com.sybd.znld.mapper.lamp.LampMapper;
import com.sybd.znld.mapper.lamp.RegionMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MapperTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private LampMapper lampMapper;

    @Test
    public void test1(){
        var map = this.regionMapper.selectAvgOfEnvironmentElementLastDayByDeviceId(533263283);
        Assert.notNull(map);
    }

    @Test
    public void test2(){
        var map = this.regionMapper.selectAvgOfEnvironmentElementLastHourByDeviceId(533263283);
        Assert.notNull(map);
    }

    @Test
    public void test3(){
        var now = LocalDateTime.now();
        var end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);
        var begin = end.minusHours(1);
        var list = this.regionMapper.selectAvgOfEnvironmentElementByDeviceId(533263283, begin, end);
        Assert.notNull(list);
    }

    @Test
    public void test4(){
        var tmp = this.lampMapper.selectEnvironmentLampByOrganId("099060a6971911e9b0790242c0a8b006");
        Assert.notNull(tmp);
    }

    @Test
    public void test5(){
        var tmp = this.lampMapper.selectLampSummary();
        Assert.notNull(tmp);
    }
}