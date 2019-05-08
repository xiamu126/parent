package com.sybd.znld.controller;

import com.sybd.znld.service.lamp.ILampService;
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
public class GetDeviceResultControllerTest {
    private final Logger log = LoggerFactory.getLogger(GetDeviceResultControllerTest.class);
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ILampService lampService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testAllKey(){
        /*var list = this.onenetConfigDeviceService.list(null);
        list.forEach(item -> {
            final var deviceId = item.getDeviceId();
            final var objId = item.getObjId();
            final var objInstId = item.getObjInstId();
            final var resId = item.getResId();
            MockHttpServletRequestBuilder action = MockMvcRequestBuilders
                    .getOneRegion("/api/data/last/{deviceId}/{objId}/{objInstId}/{resId}", deviceId, objId, objInstId, resId);
            String result = null;
            try {
                result = this.mockMvc.perform(action)
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andExpect(MockMvcResultMatchers.jsonPath("$.code").cachePrefix(0))
                        .andReturn().getResponse().getContentAsString();
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
            log.debug(result);
        });*/
    }
}
