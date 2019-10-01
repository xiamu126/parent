package com.sybd.znld.ministar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.mapper.lamp.MiniStarEffectMapper;
import com.sybd.znld.mapper.lamp.MiniStarTaskMapper;
import com.sybd.znld.mapper.lamp.RegionMapper;
import com.sybd.znld.ministar.model.SubtitleForRegion;
import com.sybd.znld.model.lamp.MiniStarEffectModel;
import com.sybd.znld.model.lamp.MiniStarTaskModel;
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
import java.time.LocalDateTime;
import java.util.List;

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
    @Autowired
    private MiniStarTaskMapper miniStarTaskMapper;

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

    @Test
    public void test7(){
        var ids = List.of(1,2,3,5,6);
        var ret = this.miniStarEffectMapper.deleteByIds("099060a6971911e9b0790242c0a8b006", ids);
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void test8(){
        var ret = this.miniStarEffectMapper.deleteById("099060a6971911e9b0790242c0a8b006", 7);
        Assert.assertEquals(1, ret);
    }

    @Test
    public void test9() throws IOException {
        var str = "{\n" +
                "\"effectIds\":[8,9,10]\n" +
                "}";
        var objectMapper = new ObjectMapper();
        var ret = objectMapper.readValue(str, Object.class);
        log.debug(ret.toString());
    }

    @Test
    public void test10(){
        var model = new MiniStarTaskModel();
        model.targetId = "5aa2ac64883611e9a7fe0242c0a8b002";
        model.targetType = 0;
        model.userId = "c9a45d5d972011e9b0790242c0a8b006";
        model.beginTime = LocalDateTime.now().plusMinutes(30);
        model.endTime = LocalDateTime.now().plusMinutes(60);
        model.organizationId = "099060a6971911e9b0790242c0a8b006";
        model.status = 0;
        var ret = this.miniStarTaskMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void test11(){
        var ret = this.miniStarTaskMapper.selectByStatusWaiting();
        Assert.assertTrue(ret != null && !ret.isEmpty());
    }

    @Test
    public void test12(){
        var ret = this.regionMapper.selectByRegionIdAndOrganId("5aa2ac64883611e9a7fe0242c0a8b002", "099060a6971911e9b0790242c0a8b006");
        Assert.assertNotNull(ret);
    }

    @Test
    public void test13(){
        var model = new MiniStarEffectModel();
        model.id = 11;
        model.name = "测试景观灯效果1";
        model.type = "呼吸灯";
        var tmp = this.miniStarEffectMapper.update(model);
        Assert.assertTrue(tmp > 0);
    }

    @Test
    public void test14(){
        var ret = this.miniStarEffectMapper.selectByOrganIdAndName("099060a6971911e9b0790242c0a8b006", "测试景观灯效果9");
        Assert.assertNotNull(ret);
    }
}
