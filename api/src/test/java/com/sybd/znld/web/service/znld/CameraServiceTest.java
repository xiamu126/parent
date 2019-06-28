package com.sybd.znld.web.service.znld;

import com.sybd.znld.model.lamp.CameraModel;
import com.sybd.znld.service.lamp.ILampService;
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
public class CameraServiceTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private ILampService lampService;

    @Test
    public void addCamera(){
        var lampId = "6aa5d6da868e11e9a1a50242c0a8f002";
        var model = new CameraModel();
        model.rtspUrl = "rtsp://admin:admin888@192.168.11.64:554/Streaming/Channels/201";
        model.rtmp = "{\"liveUrl\": \"rtmp://192.168.11.101:1935/62e36c3556a111e98edc0242ac110007/live\", \"trackUrl\": \"rtmp://localhost:1935/62e36c3556a111e98edc0242ac110007/track\"}";
        model.flvUrl = "http://192.168.11.101:8088/62e36c3556a111e98edc0242ac110007/live.flv";
        var lampCamera = this.lampService.addCamera(lampId, model);
        Assert.assertNotNull(lampCamera);
    }

    @Test
    public void removeCamera(){
        var lampId = "6aa5d6da868e11e9a1a50242c0a8f002";
        var cameraId = "327ee6f2996b11e9b0790242c0a8b006";
        var ret = this.lampService.removeCamera(lampId, cameraId);
        Assert.assertTrue(ret);
    }
}