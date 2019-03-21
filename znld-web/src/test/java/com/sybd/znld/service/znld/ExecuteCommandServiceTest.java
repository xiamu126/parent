package com.sybd.znld.service.znld;

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

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExecuteCommandServiceTest {
    private final Logger log = LoggerFactory.getLogger(ExecuteCommandServiceTest.class);

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    private ExecuteCommandService executeCommandService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void getById(){
        var ret = this.executeCommandService.getExecuteCommandById(1);
        Assert.notNull(ret);
    }
}
