package com.sybd.znld.service.znld;

import com.sybd.znld.model.znld.HttpLogModel;
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
public class LogServiceTest {
    private final Logger log = LoggerFactory.getLogger(LogServiceTest.class);
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private LogService logService;

    @Test
    public void addLog(){
        var log = new HttpLogModel();
        log.path = "api/v1/test";
        log.method = "POST";
        log.header = "";
        log.body = "";
        log.ip = "192.168.1.110";
        var ret = this.logService.addLog(log);
        Assert.notNull(ret);
    }
}
