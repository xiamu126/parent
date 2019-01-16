package com.sybd.znld.service;

import com.sybd.znld.onenet.dto.OneNetKey;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
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

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OneNetConfigDeviceServiceTest {
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
        var deviceId = 42939715;
        var ret = this.oneNetConfigDeviceService.getImeiByDeviceId(deviceId);
        Assert.assertNotNull(ret);
        Assert.assertTrue(!ret.isEmpty());
    }

    @Test
    public void getDeviceIdAndImei(){
        var ret = this.oneNetConfigDeviceService.getDeviceIdAndImeis();
        Assert.assertNotNull(ret);
    }

    @Test
    public void getDescBy(){
        Integer objId = 3304;
        Integer objInstId = 0;
        Integer resId = 5700;
        var ret = this.oneNetConfigDeviceService.getDescBy(objId,objInstId,resId);
        Assert.assertNotNull(ret);
        Assert.assertTrue(!ret.isEmpty());
        log.debug(ret);
    }

    @Test
    public void getInstanceMap(){
        var deviceId = 42939715;
        var ret = this.oneNetConfigDeviceService.getInstanceMap(deviceId);
        Assert.assertNotNull(ret);
        Assert.assertTrue(ret.size() > 0);
    }

    @Test
    public void getOneNetKey(){
        var ret = this.oneNetConfigDeviceService.getOneNetKey("wendu");
        Assert.assertNotNull(ret);
    }

    @Test
    public void getApiKeyByDeviceId(){
        var deviceId = 42939715;
        var ret = this.oneNetConfigDeviceService.getApiKeyByDeviceId(deviceId);
        Assert.assertEquals("fN8PGSJ3VoIOSoznGWuGeC25PGY=", ret);
    }

    @Test
    public void getDeviceIdNames(){
        var ret = this.oneNetConfigDeviceService.getDeviceIdNameMap();
        Assert.assertNotNull(ret);
        Assert.assertTrue(ret.size() > 0);
    }

    @Test
    public void isDataStreamIdEnabled(){
        var ret = this.oneNetConfigDeviceService.isDataStreamIdEnabled("3336_0_5516");
        Assert.assertTrue(ret);
        ret = this.oneNetConfigDeviceService.isDataStreamIdEnabled("3336_0_5517");
        Assert.assertFalse(ret);
    }
}
