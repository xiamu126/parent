package com.sybd.znld;

import com.sybd.znld.onenet.BetterOneNetService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class OneNetTest {
    @Autowired
    private BetterOneNetService oneNet;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void getDevice(){
        var tmp = this.oneNet.getDeviceById(505253765);
        log.debug(tmp.toString());
    }

    @Test
    public void getDevices(){
        var tmp = "Thu Dec 27 10:30:14 CST 2018";
        log.debug(Locale.CHINA.getCountry());
        var xxx = Locale.US;
        var time = ZonedDateTime.parse(tmp,DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss z yyyy", Locale.US));
        log.debug(tmp);
    }

    @Test
    public void getDataStreamById(){
        var tmp = this.oneNet.getLastDataStreamById(505253765, "3336_0_5518");
        log.debug(tmp.toString());
    }

    @Test
    public void getDataStreamsByIds(){
        var tmp = this.oneNet.getLastDataStreamsByIds(505253765, "3336_0_5518", "3336_0_5517");
        log.debug(tmp.toString());
    }
}
