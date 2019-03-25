package com.sybd.znld.service.znld;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class VideoConfigServiceTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    private IVideoService videoConfigService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void getById(){
        var ret = this.videoConfigService.getConfigByCameraId("30040c0e4bb011e993a60242ac110006");
        Assert.notNull(ret);
    }

    @Test
    public void setById(){
        var model = this.videoConfigService.getConfigByCameraId("30040c0e4bb011e993a60242ac110006");
        model.recordAudio = false;
        var ret = this.videoConfigService.setConfigByCameraId(model);
        Assert.notNull(ret);
        Assert.isTrue(!ret.recordAudio);
    }
}
