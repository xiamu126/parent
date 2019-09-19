package com.sybd.znld.onenet.service.ministar;

import com.sybd.znld.mapper.ministar.TwinkleBeautyGroupMapper;
import com.sybd.znld.service.ministar.IMiniStarService;
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

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TwinkleBeautyServiceTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private IMiniStarService miniStarService;

    @Autowired
    private TwinkleBeautyGroupMapper twinkleBeautyGroupMapper;

    @Test
    public void test(){
        var tmp = this.twinkleBeautyGroupMapper.selectMany("c9a45d5d972011e9b0790242c0a8b006", 10);
        Assert.assertNotNull(tmp);
    }

    @Test
    public void history(){
        var tmp = this.miniStarService.history("c9a45d5d972011e9b0790242c0a8b006", 10);
        Assert.assertNotNull(tmp);
    }
}
