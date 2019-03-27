package com.sybd.znld.service.znld;

import com.sybd.znld.model.znld.RegionModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RegionServiceTest {
    private final Logger log = LoggerFactory.getLogger(RegionServiceTest.class);

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    private RegionService regionService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void addRegion(){
        var model = new RegionModel();
        model.name = "测试区域";
        model.status = 0;
        var ret = this.regionService.addRegion(model);
        Assert.assertNotNull(ret);
        Assert.assertNotEquals("", ret.id);
    }

    @Test
    public void addRegion2(){
        for(var i = 1; i < 10; i++){
            var model = new RegionModel();
            model.name = "街道"+i;
            model.status = 0;
            var ret = this.regionService.addRegion(model);
            Assert.assertNotNull(ret);
            Assert.assertNotEquals("", ret.id);
        }
    }

    @Test
    public void modifyRegion(){
        var model = new RegionModel();
        model.id = "1884caa9495711e993a60242ac110006";
        model.name = "测试区域4";
        model.status = 0;
        var ret = this.regionService.modifyRegionById(model);
        Assert.assertNotNull(ret);
        Assert.assertNotEquals("", ret.id);
    }

    @Test
    public void getRegionById(){
        var ret = this.regionService.getRegionById("b4065088495111e993a60242ac110006");
        Assert.assertNotNull(ret);
        Assert.assertEquals("b4065088495111e993a60242ac110006", ret.id);
    }

    @Test
    public void getRegionByName(){
        var ret = this.regionService.getRegionByName("测试区域");
        Assert.assertNotNull(ret);
        Assert.assertEquals("测试区域", ret.name);
    }

    @Test
    public void getAllRegion(){
        var ret = this.regionService.getAllRegion();
        Assert.assertFalse(ret.isEmpty());
    }

    @Test
    public void getOneRegion(){
        var ret = this.regionService.getOneRegion();
        Assert.assertNotNull(ret);
    }

    @Test
    public void getRegion(){
        var ret = this.regionService.getRegion(2);
        Assert.assertEquals(2, ret.size());
    }
}
