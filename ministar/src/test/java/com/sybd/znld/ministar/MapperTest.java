package com.sybd.znld.ministar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.mapper.lamp.MiniStarEffectMapper;
import com.sybd.znld.mapper.lamp.RegionMapper;
import com.sybd.znld.ministar.model.SubtitleForRegion;
import com.sybd.znld.model.lamp.MiniStarEffectModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

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
    private MiniStarEffectMapper miniStarEffectMapper;

    @Test
    public void test1(){
        var ret = this.regionMapper.selectByOrganId("099060a6971911e9b0790242c0a8b006");
        Assert.assertTrue(ret != null && !ret.isEmpty());
    }

    @Test
    public void test2(){
        var ret = this.regionMapper.selectLampsByRegionId("5aa2ac64883611e9a7fe0242c0a8b002");
        Assert.assertTrue(ret != null && !ret.isEmpty());
    }

    @Test
    public void test3(){
        var ret = this.regionMapper.selectLampsWithLocationByRegionId("5aa2ac64883611e9a7fe0242c0a8b002");
        Assert.assertTrue(ret != null && !ret.isEmpty());
    }

    @Test
    public void test4(){
        var model = new MiniStarEffectModel();
        model.name = "test";
        model.type = "跑马灯";
        model.colors = "5500000000EE00EE00";
        model.speed = 10;
        model.brightness = 50;
        model.organizationId = "099060a6971911e9b0790242c0a8b006";
        var ret = this.miniStarEffectMapper.insert(model);
        log.debug(String.valueOf(ret));
    }

    @Test
    public void test5(){
        var ret = this.miniStarEffectMapper.selectByOrganId("099060a6971911e9b0790242c0a8b006");
        Assert.assertTrue(ret != null && ret.size() == 2);
    }

    @Test
    public void test6() throws IOException {
        var objectMapper = new ObjectMapper();
        var str = "{\"title\":\"test\",\n" +
                "\"userId\":\"c9a45d5d972011e9b0790242c0a8b006\",\n" +
                "\"regionId\":\"5aa2ac64883611e9a7fe0242c0a8b002\",\n" +
                "\"type\":1,\n" +
                "\"colors\":[{\"r\":256,\"g\":256,\"b\":\"256\"},{\"r\":256,\"g\":256,\"b\":\"256\"},{\"r\":256,\"g\":256,\"b\":\"256\"}],\n" +
                "\"speed\":10,\n" +
                "\"brightness\":50,\n" +
                "\"beginTimestamp\":1569567601000,\n" +
                "\"endTimestamp\":1569654001000}";
        var str2 = "{\n" +
                "\"title\" : \"test\",\n" +
                "\"userId\" : \"c9a45d5d972011e9b0790242c0a8b006\",\n" +
                "\"regionId\": \"5aa2ac64883611e9a7fe0242c0a8b002\",\n" +
                "\"type\" : 1,\n" +
                "\"colors\": [{\"r\":256,\"g\":256,\"b\":\"256\"},{\"r\":256,\"g\":256,\"b\":\"256\"},{\"r\":256,\"g\":256,\"b\":\"256\"}],\n" +
                "\"speed\": 10,\n" +
                "\"brightness\": 50,\n" +
                "\"beginTimestamp\": 1569567601000,\n" +
                "\"endTimestamp\": 1569654001000\n" +
                "}";
        var ret = objectMapper.readValue(str2, SubtitleForRegion.class);
        Assert.assertNotNull(ret);
    }
}
