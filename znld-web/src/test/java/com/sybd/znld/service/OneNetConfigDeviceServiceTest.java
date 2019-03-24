package com.sybd.znld.service;

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

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OneNetConfigDeviceServiceTest {
    private final Logger log = LoggerFactory.getLogger(OneNetConfigDeviceServiceTest.class);
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private OneNetConfigDeviceService oneNetConfigDeviceService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void removeAllCache(){
        this.oneNetConfigDeviceService.removeAllCache();
    }

    @Test
    public void getImeiByDeviceId(){
        int deviceId = 42939715;
        String ret = this.oneNetConfigDeviceService.getImeiByDeviceId(deviceId);
        Assert.assertNotNull(ret);
        Assert.assertTrue(!ret.isEmpty());
    }

    @Test
    public void getDeviceIdAndImei(){
        Map<Integer, String> ret = this.oneNetConfigDeviceService.getDeviceIdAndImeis();
        Assert.assertNotNull(ret);
    }

    @Test
    public void getDescBy(){
        Integer objId = 3304;
        Integer objInstId = 0;
        Integer resId = 5700;
        String ret = this.oneNetConfigDeviceService.getDescBy(objId,objInstId,resId);
        Assert.assertNotNull(ret);
        Assert.assertTrue(!ret.isEmpty());
        log.debug(ret);
    }

    @Test
    public void getInstanceMap(){
        int deviceId = 42939715;
        Map<String, String> ret = this.oneNetConfigDeviceService.getInstanceMap(deviceId);
        Assert.assertNotNull(ret);
        Assert.assertTrue(ret.size() > 0);
    }

    @Test
    public void getOneNetKey(){
        String ret = this.oneNetConfigDeviceService.getOneNetKey("wendu");
        Assert.assertNotNull(ret);
    }

    @Test
    public void getApiKeyByDeviceId(){
        int deviceId = 42939715;
        String ret = this.oneNetConfigDeviceService.getApiKeyByDeviceId(deviceId);
        Assert.assertEquals("fN8PGSJ3VoIOSoznGWuGeC25PGY=", ret);
    }

    @Test
    public void getDeviceIdNames(){
        Map<Integer, String> ret = this.oneNetConfigDeviceService.getDeviceIdNameMap();
        Assert.assertNotNull(ret);
        Assert.assertTrue(ret.size() > 0);
    }

    @Test
    public void isDataStreamIdEnabled(){
        boolean ret = this.oneNetConfigDeviceService.isDataStreamIdEnabled("3336_0_5516");
        Assert.assertTrue(ret);
        ret = this.oneNetConfigDeviceService.isDataStreamIdEnabled("3336_0_5517");
        Assert.assertFalse(ret);
    }
}
