package com.sybd.znld.ministar;

import com.sybd.znld.mapper.lamp.RegionMapper;
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
}
