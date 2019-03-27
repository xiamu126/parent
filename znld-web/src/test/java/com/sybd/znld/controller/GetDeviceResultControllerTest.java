package com.sybd.znld.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.onenet.dto.OneNetExecuteArgsEx;
import com.sybd.znld.service.OneNetConfigDeviceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetDeviceResultControllerTest {
    private final Logger log = LoggerFactory.getLogger(GetDeviceResultControllerTest.class);
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private OneNetConfigDeviceService onenetConfigDeviceService;

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

    @Test
    public void testPV(){
        OneNetExecuteArgsEx command = new OneNetExecuteArgsEx("ZNLD_PM_G");
        MockHttpServletRequestBuilder action = MockMvcRequestBuilders.post("/api/execute", command).accept(MediaType.APPLICATION_JSON_UTF8);
        ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(mapper.writeValueAsString(command)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    //.andExpect(MockMvcResultMatchers.jsonPath("$.code").cachePrefix(0))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        log.debug(result);
    }
}
