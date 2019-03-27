package com.sybd.znld.service.ministar;

import com.sybd.znld.model.ministar.TwinkleBeautyGroupModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.internal.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MiniStarServiceTest {
    private final Logger log = LoggerFactory.getLogger(MiniStarServiceTest.class);

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private IMiniStarService miniStarService;

    @Test
    public void addTwinkleBeautyGroup(){
        var model = new TwinkleBeautyGroupModel();
        model.beginTime = LocalDateTime.now().plusDays(1);
        model.endTime = model.beginTime.plusHours(1);
        model.regionId = "46b743284baf11e993a60242ac110006";
        var ret = this.miniStarService.addTwinkleBeautyGroup(model);
        Assert.notNull(ret);
    }
}
