package com.sybd.znld.environment;

import com.sybd.znld.environment.service.AQI;
import com.sybd.znld.mapper.lamp.RegionMapper;
import com.sybd.znld.util.MyDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AqiTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private RegionMapper regionMapper;

    @Test
    public void test1(){
        /*var deviceId = 533263283;
        var avgs = this.regionMapper.selectAvgOfEnvironmentElementLastHourByDeviceId(deviceId);
        Map<String, Double> map = new HashMap<>();
        avgs.forEach(a -> map.put(a.name, a.value));
        var result = AQI.of1Hour(map.get("SO2"), map.get("NO2"), map.get("CO"), map.get("O3"));
        result.at = MyDateTime.toTimestamp(avgs.get(0).at);
        log.debug(result.toString());*/
        var tmp = AQI.of24Hour(1.0, 1.0, 1.0, 1.0, 38.0, 1.0,1.0);
        log.debug("");
    }
}
